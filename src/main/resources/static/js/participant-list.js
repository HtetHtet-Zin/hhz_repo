const tableBody = document.getElementById("staffTableBody");
const pagination = document.getElementById("pagination");
const searchInput = document.getElementById("search");
const countElem = document.getElementById('count');
const participantElem = document.getElementById('participant-text');
const selectBox = document.getElementById("activity");
const searchBtn = document.getElementById("searchBtn");

let currentPage = 0;
let currentKeyword = "";
let currentEventName = "";

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
    currentEventName = selectBox.value;
    currentPage = 0;
    loadStaffData();
});

function loadStaffData() {

    fetch("/club/participant-list", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            eventName: currentEventName,
            keyword: currentKeyword,
            page: currentPage
        })
    })
    .then(res => res.json())
    .then(data => {
        participantCount(data.page.totalElements);
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
    const count = data.page.totalElements;
    document.getElementById('export').disabled = !count > 0;
    tableBody.innerHTML = "";

    if (!data.content || data.content.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align:center;">No participants yet</td>
            </tr>
        `;
        return;
    }

    data.content.forEach((event, index) => {
        tableBody.innerHTML += `
            <tr>
                <td>${(data.page.number * data.page.size) + index + 1}</td>
                <td>${event.eventName}</td>
                <td>${event.date} (${event.startTime} - ${event.endTime})</td>
                <td>${event.staffNo}</td>
                <td>${event.staffName}</td>
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

function exportData(keyword, eventName){
    console.log('keyword and event name', keyword, eventName);
    const formData = new FormData();
    formData.append('keyword', keyword);
    formData.append('eventName', eventName);
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