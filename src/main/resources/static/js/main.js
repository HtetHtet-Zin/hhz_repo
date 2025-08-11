document.querySelectorAll('a').forEach(a => {
    if((window.location.href + "'").includes(filterLinkFromOnClick(a))){
        a.classList.add('active-link');
    }
});

function filterLinkFromOnClick(element){
    return (element.onclick + "").trim().replace("function onclick(event) {\nwindow.location.href='", "").replace("';\n}", "") + "'";
}