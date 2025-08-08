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
        document.getElementById('count').textContent = data.totalElements;
        renderTable(data);
        renderPagination(data);
    })
    .catch(err => console.error("Error loading staff:", err));
}

function renderTable(data) {
    tableBody.innerHTML = "";
    data.content.forEach((staff, index) => {
        tableBody.innerHTML += `
            <tr>
                <td>${(data.number * data.size) + index + 1}</td>
                <td>${staff.staffNo}</td>
                <td>${staff.staffName}</td>
                <td>${staff.email}</td>
                <td>${staff.mobile}</td>
                <td>${staff.dob ?? ""}</td>
                <td>${staff.eventName}</td>
            </tr>
        `;
    });
}

function renderPagination(data) {
    pagination.innerHTML = "";

    // Previous button
    pagination.innerHTML += `
        <li class="page-item ${data.first ? 'disabled' : ''}">
            <a class="page-link"  onclick="changePage(${data.number - 1})">Previous</a>
        </li>
    `;

    // Page numbers
    const start = Math.max(0, Math.min(data.totalPages - 5, data.number - 2));
    const end = Math.min(data.totalPages - 1, Math.max(4, data.number + 2));
    for (let i = start; i <= end; i++) {
        pagination.innerHTML += `
            <li class="page-item ${i === data.number ? 'active' : ''}">
                <a class="page-link" onclick="changePage(${i})">${i + 1}</a>
            </li>
        `;
    }

    // Next button
    pagination.innerHTML += `
        <li class="page-item ${data.last ? 'disabled' : ''}">
            <a class="page-link" onclick="changePage(${data.number + 1})">Next</a>
        </li>
    `;
}

function changePage(page) {
    if (page < 0) return;
    currentPage = page;
    loadStaffData();
}
