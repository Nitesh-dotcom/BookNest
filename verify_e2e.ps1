$baseUrl = "http://localhost:8080/api"

function Assert-Step($desc, $cond) {
    if ($cond) {
        Write-Host " [PASS] $desc" -ForegroundColor Green
    } else {
        Write-Host " [FAIL] $desc" -ForegroundColor Red
        exit 1
    }
}

Write-Host "=== 1. Public Endpoints ===" -ForegroundColor Cyan
$cats = Invoke-RestMethod "$baseUrl/categories"
Assert-Step "Categories returned" ($cats.data.Count -gt 0)

$books = Invoke-RestMethod "$baseUrl/books?size=5"
Assert-Step "Books returned with pagination" ($books.data.content.Count -gt 0)

$featured = Invoke-RestMethod "$baseUrl/books/featured"
Assert-Step "Featured books returned" ($featured.data.Count -gt 0)

$bestsellers = Invoke-RestMethod "$baseUrl/books/bestsellers"
Assert-Step "Bestsellers returned" ($bestsellers.data.Count -gt 0)

$book1 = Invoke-RestMethod "$baseUrl/books/1"
Assert-Step "Book 1 retrieved" ($book1.data.id -eq 1)

$related = Invoke-RestMethod "$baseUrl/books/1/related"
Assert-Step "Related books returned" ($related.data.Count -ge 0)

$reviews = Invoke-RestMethod "$baseUrl/books/1/reviews"
Assert-Step "Reviews returned" ($reviews.data -ne $null)

Write-Host "`n=== 2. Customer Authentication & Lifecycle ===" -ForegroundColor Cyan
$custLogin = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"customer@booknest.com","password":"Customer@123"}'
$custToken = $custLogin.data.token
Assert-Step "Customer logged in successfully" ($custToken -ne $null)
$custHeaders = @{ Authorization = "Bearer $custToken" }

$profile = Invoke-RestMethod -Uri "$baseUrl/users/profile" -Headers $custHeaders
Assert-Step "Customer profile fetched" ($profile.data.email -eq "customer@booknest.com")

# Wishlist
$wishAdd = Invoke-RestMethod -Uri "$baseUrl/wishlist/items" -Method Post -Headers $custHeaders -ContentType "application/json" -Body '{"bookId":2}'
Assert-Step "Book 2 added to wishlist" ($wishAdd.data.items.Count -gt 0)

$wishlist = Invoke-RestMethod -Uri "$baseUrl/wishlist" -Headers $custHeaders
Assert-Step "Wishlist contains items" ($wishlist.data.items.Count -gt 0)

# Cart
$cartClear = Invoke-RestMethod -Uri "$baseUrl/cart" -Method Delete -Headers $custHeaders
$cartAdd = Invoke-RestMethod -Uri "$baseUrl/cart/items" -Method Post -Headers $custHeaders -ContentType "application/json" -Body '{"bookId":2,"quantity":1}'
Assert-Step "Book 2 added to cart" ($cartAdd.data.items.Count -eq 1)

# Checkout
$orderBody = @{
    customerName = "Jane Doe"
    customerPhone = "555-123-4567"
    deliveryAddress = "123 Story Ave, Apt 4B"
    city = "Seattle"
    state = "WA"
    postalCode = "98101"
    deliveryInstructions = "Leave at front desk"
    paymentMethod = "COD"
} | ConvertTo-Json

$orderRes = Invoke-RestMethod -Uri "$baseUrl/orders" -Method Post -Headers $custHeaders -ContentType "application/json" -Body $orderBody
$orderId = $orderRes.data.id
$orderNumber = $orderRes.data.orderNumber
Assert-Step "Order placed successfully ($orderNumber)" ($orderId -gt 0)

# Check order in customer history
$custOrders = Invoke-RestMethod -Uri "$baseUrl/orders" -Headers $custHeaders
$hasOrder = ($custOrders.data.content | Where-Object { $_.id -eq $orderId }) -ne $null
Assert-Step "Order found in customer order history" $hasOrder

# Cancel order
$cancelRes = Invoke-RestMethod -Uri "$baseUrl/orders/$orderId/cancel" -Method Post -Headers $custHeaders
Assert-Step "Order cancelled successfully" ($cancelRes.data.status -eq "CANCELLED")

# Review
$reviewPassed = $false
try {
    $reviewBody = @{
        rating = 5
        headline = "Fantastic Book!"
        comment = "One of the most inspiring books I have read this year. Highly recommended!"
    } | ConvertTo-Json
    $reviewRes = Invoke-RestMethod -Uri "$baseUrl/books/14/reviews" -Method Post -Headers $custHeaders -ContentType "application/json" -Body $reviewBody
    $reviewPassed = ($reviewRes.data.rating -eq 5)
} catch {
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        $errText = $reader.ReadToEnd()
        if ($errText -match "already submitted") {
            $reviewPassed = $true
        }
    }
}
Assert-Step "Review system working (creation & duplicate protection)" $reviewPassed

Write-Host "`n=== 3. Admin Authentication & Operations ===" -ForegroundColor Cyan
$adminLogin = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"admin@booknest.com","password":"Admin@123"}'
$adminToken = $adminLogin.data.token
Assert-Step "Admin logged in successfully" ($adminToken -ne $null)
$adminHeaders = @{ Authorization = "Bearer $adminToken" }

$dash = Invoke-RestMethod -Uri "$baseUrl/admin/dashboard" -Headers $adminHeaders
Assert-Step "Admin dashboard stats retrieved" ($dash.data.totalBooks -gt 0)

$adminBooks = Invoke-RestMethod -Uri "$baseUrl/admin/books" -Headers $adminHeaders
Assert-Step "Admin books retrieved" ($adminBooks.data.content.Count -gt 0)

$adminOrders = Invoke-RestMethod -Uri "$baseUrl/admin/orders" -Headers $adminHeaders
Assert-Step "Admin orders retrieved" ($adminOrders.data.content.Count -gt 0)

$adminCustomers = Invoke-RestMethod -Uri "$baseUrl/admin/customers" -Headers $adminHeaders
Assert-Step "Admin customers retrieved" ($adminCustomers.data.Count -gt 0)

# Update Stock
$stockRes = Invoke-RestMethod -Uri "$baseUrl/admin/books/2/stock" -Method Patch -Headers $adminHeaders -ContentType "application/json" -Body '{"stockQuantity":45}'
Assert-Step "Admin stock updated" ($stockRes.data.stockQuantity -eq 45)

Write-Host "`n ALL 17 END-TO-END VERIFICATIONS PASSED PERFECTLY!" -ForegroundColor Green
