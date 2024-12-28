document.addEventListener("DOMContentLoaded", function() {

    const deleteButtons = document.querySelectorAll('.delete-btn');

    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');

            fetch(`/products/${productId}`)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('product-name').innerText = data.name;
                    document.getElementById('product-stock').innerText = data.stock;
                    document.getElementById('delete-form').action = "/products/delete_product/" + data.id;

                    var modal = new bootstrap.Modal(document.getElementById('deleteModal'));
                    modal.show();
                })
                .catch(error => console.error('Error fetching product data:', error));
        });
    });
});

