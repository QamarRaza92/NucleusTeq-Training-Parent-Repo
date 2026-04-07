// --- State Management ---
// Default data agar pehli baar app khul rahi hai
const defaultProducts = [
    { id: 1710000001, name: "Wireless Mouse", price: 1200, stock: 15, category: "electronics" },
    { id: 1710000002, name: "Mechanical Keyboard", price: 3500, stock: 4, category: "electronics" }, // Low stock
    { id: 1710000003, name: "Cotton T-Shirt", price: 500, stock: 20, category: "clothing" },
    { id: 1710000004, name: "Denim Jeans", price: 1500, stock: 8, category: "clothing" },
    { id: 1710000005, name: "JavaScript Crash Course", price: 800, stock: 0, category: "books" }, // Out of stock
    { id: 1710000006, name: "Atomic Habits", price: 400, stock: 12, category: "books" },
    { id: 1710000007, name: "Leather Wallet", price: 900, stock: 5, category: "accessories" },
    { id: 1710000008, name: "Aviator Sunglasses", price: 1200, stock: 2, category: "accessories" } // Low stock
];

let inventory = []; // Hamara main array jo poori app mein use hoga

// --- DOM Elements ko target karna ---
const loadingMessage = document.getElementById('loadingMessage');
const productGrid = document.getElementById('productGrid');

//  --- LocalStorage Logic ---
function initData() {
    // Check karte hain ki pehle se data save hai ya nahi
    const savedData = localStorage.getItem('inventoryData');
    
    if (savedData) {
        inventory = JSON.parse(savedData);
    } else {
        // Agar khali hai toh default products daal do
        inventory = [...defaultProducts];
        saveToLocalStorage();
    }
}

function saveToLocalStorage() {
    localStorage.setItem('inventoryData', JSON.stringify(inventory));
}

// --- Async API Simulation ---
function fetchProducts() {
    return new Promise((resolve) => {
        // 1.5 seconds ka fake server delay create kar rahe hain
        setTimeout(() => {
            resolve(inventory);
        }, 1500);
    });
}

// --- App Initialization ---
async function startDashboard() {
    initData(); // Pehle localStorage se data uthao

    try {
        // Fake API call ko wait karo
        const data = await fetchProducts();
        
        // Data aane ke baad loading screen ko hatao aur Grid ko dikhao
        loadingMessage.classList.add('hidden');
        productGrid.classList.remove('hidden');

        console.log("Products successfully loaded!", data);
        
        // --- FINAL FIX YAHAN HAI ---
        applyAllFilters(); // Ye apne aap products render kar dega
        updateAnalytics(); // Ye boxes mein 0 ki jagah actual values daal dega

    } catch (error) {
        console.error("Data fetch karne mein problem aayi:", error);
        loadingMessage.innerHTML = "<p style='color: red;'>Failed to load products.</p>";
    }
}

document.addEventListener('DOMContentLoaded', startDashboard);


// Ye function array ko HTML cards mein convert karke screen par dikhayega
function renderProducts(productsArray) {
    // Sabse pehle grid ko khali karo, to avoid duplicating purana data
    productGrid.innerHTML = '';

    // Agar array khali hai (jaise filter karne par kuch na mile), toh message dikhao
    if (productsArray.length === 0) {
        productGrid.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 20px; color: #7f8c8d;">
                <h3>No products found!</h3>
                <p>Try adjusting your search or filters.</p>
            </div>
        `;
        return; 
    }

    // Array ke har item par loop chala kar card DOM element banao
    productsArray.forEach(product => {
        const card = document.createElement('div');
        card.className = 'product-card';

        // Card ka andar ka HTML structure (Template literals use karke variables inject kiye hain)
        card.innerHTML = `
            <h3>${product.name}</h3>
            <span class="category-badge">${product.category.toUpperCase()}</span>
            <p><strong>Price:</strong> ₹${product.price}</p>
            <p><strong>Stock:</strong> 
                <span style="color: ${product.stock < 5 ? 'red' : 'green'}; font-weight: bold;">
                    ${product.stock} units
                </span>
            </p>
            <button class="delete-btn" onclick="deleteProduct(${product.id})">Delete</button>
        `;

        // Tayyar card ko main product grid mein jod do
        productGrid.appendChild(card);
    });
}

// --- Filtering & Sorting Logic ---

const searchInput = document.getElementById('searchInput');
const categoryFilter = document.getElementById('categoryFilter');
// Naye elements target kar rahe hain
const lowStockBtn = document.getElementById('lowStockBtn');
const sortDropdown = document.getElementById('sortDropdown');

// Low stock filter ka ek switch (Flag) bana liya
let isLowStockOnly = false;

// Event listeners lagana
searchInput.addEventListener('input', applyAllFilters);
categoryFilter.addEventListener('change', applyAllFilters);
sortDropdown.addEventListener('change', applyAllFilters);

// Button click hone par state toggle karo aur filter chalao
lowStockBtn.addEventListener('click', () => {
    isLowStockOnly = !isLowStockOnly; // True ko false, false ko true kar dega
    
    // UI update: Button lal ya wapas normal karne ke liye CSS class add/remove
    if (isLowStockOnly) {
        lowStockBtn.classList.add('active');
    } else {
        lowStockBtn.classList.remove('active');
    }
    
    applyAllFilters();
});

// Master function jo filter aur sort dono handle karega
function applyAllFilters() {
    const searchText = searchInput.value.toLowerCase();
    const selectedCategory = categoryFilter.value;
    const sortValue = sortDropdown.value;

    // 1. FILTERING
    let processedProducts = inventory.filter(item => {
        const matchName = item.name.toLowerCase().includes(searchText);
        const matchCategory = (selectedCategory === 'all') || (item.category === selectedCategory);
        
        // Agar button ON hai, toh stock < 5 check karo, warna sabko pass hone do (true)
        const matchStock = isLowStockOnly ? (item.stock < 5) : true;
        
        return matchName && matchCategory && matchStock;
    });

    // 2. SORTING
    // Array ka .sort() method use karke order change kar rahe hain
    switch (sortValue) {
        case 'price-asc':
            processedProducts.sort((a, b) => a.price - b.price); // Kam se zyada
            break;
        case 'price-desc':
            processedProducts.sort((a, b) => b.price - a.price); // Zyada se kam
            break;
        case 'name-asc':
            processedProducts.sort((a, b) => a.name.localeCompare(b.name)); // A se Z
            break;
        case 'name-desc':
            processedProducts.sort((a, b) => b.name.localeCompare(a.name)); // Z se A
            break;
        default:
            // Default mein kuch nai karna, original order chalne do
            break;
    }

    // Filter aur Sort hone ke baad jo final list bachi, usko UI par bhej do
    renderProducts(processedProducts);
}


// --- Analytics Dashboard Logic ---

function updateAnalytics() {
    // 1. Total Products
    const totalProducts = inventory.length;
    
    // 2. Total Value (Reduce method se Price * Stock ko jodd rahe hain)
    const totalValue = inventory.reduce((sum, item) => {
        return sum + (item.price * item.stock);
    }, 0);

    // 3. Out of Stock (Filter karke check kar rahe hain kitne 0 stock wale hain)
    const outOfStock = inventory.filter(item => item.stock === 0).length;

    // UI ko update karna
    document.getElementById('totalProductsCount').textContent = totalProducts;
    // .toLocaleString() se number thoda sundar dikhega (e.g. 50,000)
    document.getElementById('totalInventoryValue').textContent = `₹${totalValue.toLocaleString('en-IN')}`;
    document.getElementById('outOfStockCount').textContent = outOfStock;
}

// --- Product Management (Add & Delete) ---

// Delete Product Function
function deleteProduct(productId) {
    // Confirm box ek achhi practice hai taaki galti se click hone par seedha delete na ho
    if(confirm("Are you sure you want to delete this product?")) {
        // Filter karke us ID wale product ko bahar nikal do
        inventory = inventory.filter(item => item.id !== productId);
        
        // Data save karo aur poori UI ko refresh maar do
        saveToLocalStorage();
        applyAllFilters(); 
        updateAnalytics();
    }
}

// Add New Product Function
const addProductForm = document.getElementById('addProductForm');

addProductForm.addEventListener('submit', function(event) {
    event.preventDefault(); // Default page refresh ko roko

    // Form se values nikalna aur extra spaces hatana (.trim)
    const nameInput = document.getElementById('prodName').value.trim();
    const priceInput = parseFloat(document.getElementById('prodPrice').value);
    const stockInput = parseInt(document.getElementById('prodStock').value);
    const categoryInput = document.getElementById('prodCategory').value;

    // Extra Validation check
    if (!nameInput || priceInput <= 0 || stockInput < 0 || !categoryInput) {
        alert("Please check your inputs! Price must be > 0 and stock cannot be negative.");
        return;
    }

    // Naya product object banana
    const newProduct = {
        id: Date.now(), // Unique ID banane ke liye
        name: nameInput,
        price: priceInput,
        stock: stockInput,
        category: categoryInput
    };

    // Main array mein naya product daal do
    inventory.push(newProduct);
    
    // Storage, UI aur Analytics sab update kar do
    saveToLocalStorage();
    applyAllFilters();
    updateAnalytics();

    // Form ko wapas blank kar do agle use ke liye
    addProductForm.reset();
    
    // User ko bata do ki kaam ho gaya
    alert(`${nameInput} added successfully to the inventory!`);
});