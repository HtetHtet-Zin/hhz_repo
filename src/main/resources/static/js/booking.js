const tableBody = document.getElementById("staffTableBody");
const pagination = document.getElementById("pagination");
const searchInput = document.getElementById("search");
const countElem = document.getElementById('count');
const participantElem = document.getElementById('participant-text');
const searchBtn = document.getElementById("searchBtn");

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

searchBtn.addEventListener("click", () => {
    currentKeyword = searchInput.value.trim();
    currentPage = 0;
    loadStaffData();
});

function loadStaffData() {

    fetch(`${participantUrl}`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            keyword: currentKeyword,
            page: currentPage
        })
    })
    .then(res => res.json())
    .then(data => {
        participantCount(data.page.totalElements);
        console.log(data.content);
        renderTable(data);
        renderPagination(data.page);
    })
    .catch(err => console.error("Error loading event:", err));
}

function participantCount(total) {
    if (total === 0) {
        countElem.textContent = "no";
        participantElem.textContent = "participants";
    } else if (total === 1) {
        countElem.textContent = "1";
        participantElem.textContent = "participant";
    } else {
        countElem.textContent = total;
        participantElem.textContent = "participants";
    }
}

function renderTable(data) {
console.log("Data - l");
    const count = data.page.totalElements;
    tableBody.innerHTML = "";

    if (!data.content || data.content.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="${thCount}" style="text-align:center;">No participants yet</td>
            </tr>
        `;
        return;
    }

console.log("Data - ", data.content);
    data.content.forEach((booking, index) => {
        tableBody.innerHTML += `
            <tr>
                <td>${(data.page.number * data.page.size) + index + 1}</td>
               <td>${booking.eventName}</td>
                   <td>${booking.date}</td>
                    <td>${booking.startTime} - ${booking.endTime}</td>
                    <td>${booking.name}</td>
                    <td>${booking.status}</td>
                   <td>${booking.bookedDate}</td>
                   <td>${booking.team}</td>
                <td>${booking.department}</td>
                 <td style="text-align:center;">
                      <input type="checkbox" class="admin-flag-toggle"
                              data-id="${booking.id}"
                   >
                  </td>
            </tr>
        `;
    });
}


function renderPagination(page) {
    pagination.innerHTML = "";
    if (page.totalElements == 0) {
        return;
    }

    // Previous button
    pagination.innerHTML += `
        <li class="page-item ${page.number == 0 ? 'disabled' : ''}">
            <a class="page-link"  onclick="changePage(${page.number - 1})">&#10094;&#10094;</a>
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
