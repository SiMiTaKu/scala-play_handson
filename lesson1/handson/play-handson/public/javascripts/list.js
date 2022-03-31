
document.addEventListener("DOMContentLoaded",function(){
    Array.from(
        document.getElementsByClassName("delete")
    ).forEach(action => {
        action.addEventListener("click", (e) => {
            e.currentTarget.parentNode.submit();
            e.stopPropagation();
        });
    });

    Array.from(
        document.getElementsByClassName("card")
    ).forEach(card => {
        card.addEventListener("click", (e) => {
            location.href = e.currentTarget.getAttribute("data-href");
        })
    })
});