const tableBody = document.getElementById("staffTableBody");
const pagination = document.getElementById("pagination");
const searchInput = document.getElementById("search");

const table = tableBody.closest('table');
const thCount = table.querySelectorAll('th').length;

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
    fetch("/club/staffs", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            keyword: currentKeyword,
            page: currentPage
        })
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById('count').textContent = data.page.totalElements;
        renderTable(data);
        renderPagination(data.page);
    })
    .catch(err => console.error("Error loading staff:", err));
}

function renderTable(data) {
    tableBody.innerHTML = "";
    if (!data.content || data.content.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="${thCount}" style="text-align:center;">No staffs yet</td>
            </tr>
        `;
        return;
    }

    data.content.forEach((staff, index) => {
        tableBody.innerHTML += `
            <tr>
                <td>${(data.page.number * data.page.size) + index + 1}</td>
                <td>${staff.staffNo}</td>
                <td>${staff.name}</td>
                <td>${staff.email}</td>
                <td>${staff.team}</td>
                <td>${staff.department}</td>
                <td>${staff.mobile}</td>
                <td style="text-align:center;">
                    <input hx-post="/club/staff/admin-flag"
                        hx-vals='js:{"staffNo": "${staff.staffNo}", "adminFlag": event.target.checked}'
                        type="checkbox"
                    ${staff.adminFlag?'checked':''}>
                </td>
                <td style="text-align:center;">
                    <input hx-post="/club/staff/approver-flag"
                        hx-vals='js:{"staffNo": "${staff.staffNo}", "approverFlag": event.target.checked}'
                        type="checkbox"
                    ${staff.approverFlag?'checked':''}>
                </td>
            </tr>
        `;
    });
    htmx.process(tableBody);
}

function renderPagination(page) {
    pagination.innerHTML = "";
    if (page.totalElements == 0) {
        return;
    }

    // Previous button
    pagination.innerHTML += `
        <li class="page-item ${page.number == 0 ? 'disabled' : ''}">
            <a class="page-link" onclick="changePage(${page.number - 1})">&#10094;&#10094;</a>
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
            <a class="page-link" onclick="changePage(${page.number + 1})">&#10095;&#10095;</a>
        </li>
    `;
}

function changePage(page) {
    if (page < 0) return;
    currentPage = page;
    loadStaffData();
}