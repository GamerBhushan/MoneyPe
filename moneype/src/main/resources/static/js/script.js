function routeTo(path) {
    window.location.href = path; // Redirects to the specified route
}

function exitApp() {
    if (confirm("Are you sure you want to exit?")) {
        window.close(); // This will close the window in some browsers
    }
}


function performAction(action) {
    fetch('MoneyPeServlet', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'action=' + action
    })
    .then(response => response.text())
    .then(data => {
        document.getElementById("output").innerText = data;
    });
}
