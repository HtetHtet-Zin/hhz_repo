const tableBody = document.getElementById("staffTableBody");
const pagination = document.getElementById("pagination");
const searchInput = document.getElementById("search");

let currentPage = 0;
let currentKeyword = "";

document.addEventListener("DOMContentLoaded", () => {
    loadStaffData();
});

searchInput.addEventListener("input", () => {
    currentKeyword = searchInput.value.trim();
    currentPage = 0;
    loadStaffData();
});

function loadStaffData() {
    fetch("/club/event-list", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            keyword: currentKeyword,
            page: currentPage
        })
    })
    .then(res => res.json())
    .then(data => {
        renderTable(data);
        renderPagination(data.page);
    })
    .catch(err => console.error("Error loading event:", err));
}

function renderTable(data) {
    const count = data.page.totalElements;
    document.getElementById('count').textContent = count;
    document.getElementById('export').disabled = !count > 0;
    tableBody.innerHTML = "";
    data.content.forEach((event, index) => {
        tableBody.innerHTML += `
            <tr>
                <td>${(data.page.number * data.page.size) + index + 1}</td>
                <td>${event.eventName}</td>
                <td>${event.date}</td>
                <td>${event.startTime} - ${event.endTime}</td>
                <td>${event.staffNo}</td>
                <td>${event.staffName}</td>
            </tr>
        `;
    });
}

function renderPagination(page) {
    pagination.innerHTML = "";

    // Previous button
    pagination.innerHTML += `
        <li class="page-item ${page.number == 0 ? 'disabled' : ''}">
            <a class="page-link"  onclick="changePage(${page.number - 1})"><<</a>
        </li>
    `;

    // Page numbers
    const start = Math.max(0, Math.min(page.totalPages - 5, page.number - 2));
    const end = Math.min(page.totalPages - 1, Math.max(4, page.number + 2));
    for (let i = start; i <= end; i++) {
        pagination.innerHTML += `
            <li class="page-item ${i === page.number ? 'active' : ''}">
                <a class="page-link" onclick="changePage(${i})">${i + 1}</a>
            </li>
        `;
    }

    // Next button
    pagination.innerHTML += `
        <li class="page-item ${page.number + 1 == page.totalPages ? 'disabled' : ''}">
            <a class="page-link" onclick="changePage(${page.number + 1})">>></a>
        </li>
    `;
}

function changePage(page) {
    if (page < 0) return;
    currentPage = page;
    loadStaffData();
}

function exportData(keyword){
    console.log('keyword', keyword);
    const formData = new FormData();
    formData.append('keyword', keyword);
    fetch(`${exportUrl}`, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Network response was not ok");
        }
        return response.blob();
    })
    .then(blob => {
        if(blob.type != "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"){
            throw new Error("Does not match response file type.")
        }
        // Trigger the download
        const fileUrl = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.style.display = "none";
        a.href = fileUrl;
        a.download = 'DAT-Event-System - Staff in event list.xlsx';
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(fileUrl);
    })
    .catch(error => {
        console.error(error);
    });
}