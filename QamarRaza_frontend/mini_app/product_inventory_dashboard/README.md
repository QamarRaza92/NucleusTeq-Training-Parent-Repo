# Product Inventory Dashboard

## About This Project
This is my Product Inventory Dashboard. I created this project entirely from scratch using just regular HTML, CSS, and JavaScript. I used core html and css to built this project

Basically, here you can manage product list. The updataion on th dashboard is also kind of smooth which enhances user visuals. Also i also used 'localStorage' to save data from being erased.

## Features developed:
* Fake API Delay: The js 'setTimeout' was used here along with js promises to show a "Loading..." screen initially when the site is
opened.

* Dynamic Cards: I did not write product cards in HTML. UI is generated dynamically by my js code.

* Search & Filters: - UpperCase and LowerCase are ignored while searching in the search bar.
  - To sort items, i added a category dropdown.
  - Also there is a "Low Stock" button to quickly filters out items with a stock count of less than 5.

* Sorting: We can arrange the product by price or in alphabetical order.

* Live Stats: The secction on top would act a mini-dashboard. We can see total inventory items and total price in the beginning.

* Adding/Deleting: There is also an option to either add or delete new product. 

## How to test it
It's easy to run as no server setup is required.
1. First try to download the or clone the folder.
2. Then move to 'product_inventory_dashboard' folder.
3. Now need to open the index.html file.



## Screenshots
The Snapshots of working apps are here

1. The Main View
![Dashboard Layout](./all_screenshots/dashboard_main.png)
> The first view of the site when it loads.

2. Using the Filters
![Using Filter](./all_screenshots/using_filter.png)
> Here I clicked the Low Stock filter and sorted the items by price. The grid updates immediately.

3. The "Add Product" Form
![Add Product Form](./all_screenshots/add_product_form.png)
> This is the form at the bottom. It checks for wrong inputs before adding the item to the list.

4. Adding new product demo
![Adding New Product](./all_screenshots/adding_new_product.png)
In this screenshot, i used the functionality to add a new product

5. Deleting Product Option
![Deleting Product](./all_screenshots/deleting_product_option.png)
Here i have shown the option through which we can delete any product

6. Different sorting filters
![Sorting Filters](./all_screenshots/different_sorting_filters.png)
I applied different sorting filters here

7. Different category Filters
![Categoty Filters](./all_screenshots/different_category_filters.png)
This screenshots shows the different types of categories which are available such as electronics, accesories etc.

8. Local Storage
![Local Storage](./all_screenshots/local_storage.png)
Also a local storage screenshot has been added there to view the local storage

9. Using Multiple Filters
![Multiple Filters](./all_screenshots/multiple_filters.png)
In this screenshot, i applied many filters toghether, being more precise i chose category="electronics", stock="less than 5" and
soting="Alphabetically A-Z"

10. Pop up message
![Success Message](./all_screenshots/successful_message.png)
This is a pop up message screenshot which is popped up when a new product has been added successfully

11. Workinng of searchbar
![Searchbar Working](./all_screenshots/searchbar_working.png)
Here i am using the searchbar to search a product