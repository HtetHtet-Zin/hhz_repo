// Loader container
const loaderContainer = document.createElement("div");
loaderContainer.style.position = "fixed";
loaderContainer.style.top = "50%";
loaderContainer.style.left = "50%";
loaderContainer.style.transform = "translate(-50%, -50%)";
loaderContainer.style.display = "none";
loaderContainer.style.zIndex = "100000"; // above overlay
document.body.appendChild(loaderContainer);

// Loader spinner
const loader = document.createElement("div");
loader.style.width = "80px";
loader.style.height = "80px";
loader.style.border = "8px solid #f3f3f3";
loader.style.borderTop = "8px solid #3498db";
loader.style.borderRadius = "50%";
loader.style.animation = "spin 1s linear infinite";
loaderContainer.appendChild(loader);

// Overlay
const overlay = document.createElement("div");
overlay.style.position = "fixed";
overlay.style.top = "0";
overlay.style.left = "0";
overlay.style.width = "100%";
overlay.style.height = "100%";
overlay.style.backgroundColor = "rgba(0, 0, 0, 0.3)";
overlay.style.zIndex = "99999";
overlay.style.display = "none";
document.body.appendChild(overlay);

let isLoading = false;
//
//function startLoading() {
//    if(isLoading) return;
//    isLoading=true;
//
//    loader.style.display = "block";
//    overlay.style.display = "block";
//    document.body.style.pointerEvents = "none";
//
////      document.querySelectorAll("button").forEach(btn => btn.disabled = true);
////       // Freeze the screen
////          document.body.style.pointerEvents = "none"; // Disable all clicks
////          document.body.style.overflow = "hidden";
//}
//
//function stopLoading() {
//
//    isLoading=false;
//    loader.style.display = "none";
//    overlay.style.display = "none";
//    document.body.style.pointerEvents = "auto";
////     document.querySelectorAll("button").forEach(btn => btn.disabled = false);
////     document.body.style.pointerEvents = "auto"; // Restore clicks
////         document.body.style.overflow = "auto";
//}

function startLoading() {
if(isLoading) return;
    isLoading=true;
  loaderContainer.style.display = "block";
  overlay.style.display = "block";
}

function stopLoading() {
    isLoading=false;
  loaderContainer.style.display = "none";
  overlay.style.display = "none";
}

// Add CSS keyframes
const style = document.createElement("style");
style.innerHTML = `
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}`;
document.head.appendChild(style);
