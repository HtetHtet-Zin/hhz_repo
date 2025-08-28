const tableBody = document.getElementById("scheduleTableBody");
const pagination = document.getElementById("pagination");
const searchInput = document.getElementById("search");
const eventId = document.getElementById("eventId").value;
const countElem = document.getElementById('count');
const activityElem = document.getElementById('activity-text');
let currentPage = 0;
let currentKeyword = "";

const table = tableBody.closest('table');
const thCount = table.querySelectorAll('th').length;

document.addEventListener("DOMContentLoaded", () => {
    loadData();
});

searchInput.addEventListener("input", () => {
    currentKeyword = searchInput.value.trim();
    currentPage = 0;
    loadData();
});

function activityCount(total) {
    if (total === 0) {
        countElem.textContent = "no";
        activityElem.textContent = "activities";
    } else if (total === 1) {
        countElem.textContent = "1";
        activityElem.textContent = "activity";
    } else {
        countElem.textContent = total;
        activityElem.textContent = "activities";
    }
}

function loadData() {
    fetch(`${scheduleUrl}`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            keyword: currentKeyword,
            page: currentPage,
            eventId: eventId
        })
    })
    .then(res => res.json())
    .then(data => {
        activityCount(data.page.totalElements);
        renderTable(data);
        renderPagination(data.page);
    })
    .catch(err => console.error("Error loading event-schedule:", err));
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
    loadData();
}

function renderTable(data) {
    const count = data.page.totalElements;
    tableBody.innerHTML = "";
    if (!data.content || data.content.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="${thCount}" style="text-align:center;">No schedules yet</td>
            </tr>
        `;
        return;
    }

    data.content.forEach((schedule, index) => {
        let btnHtml = "";


        if ( schedule.bookingFlag === null && handleBookingDate(schedule.date,schedule.bookingFlag) ) {
            btnHtml = `
                <button class="book-btn"
                        data-id="${schedule.id}"
                        data-date="${schedule.date}"
                        data-start="${schedule.fromTime}"
                        data-end="${schedule.toTime}"
                        onclick="handleBookingClick(this)">
                    <i class="bi bi-bookmark-check-fill"></i>
                </button>
            `;
        } else if (schedule.bookingFlag === false && handleBookingDate(schedule.date,schedule.bookingFlag) ) {
            btnHtml = `
                <button class="book-btn">
                    <i class="bi bi-hourglass-split"></i>
                </button>
            `;
        } else if (schedule.bookingFlag === true) {
            btnHtml = `
                <button class="book-btn" disabled>
                    <i class="bi bi-check-circle-fill"></i>
                </button>
            `;
        }

        tableBody.innerHTML += `
            <tr>
                <td>${(data.page.number * data.page.size) + index + 1}</td>
                <td>${schedule.name}</td>
                <td>${schedule.date} (${schedule.fromTime} - ${schedule.toTime})</td>
                <td>${btnHtml}</td>
            </tr>
        `;
    });
}

function handleBookingDate(bookingDate,bookingFlag){
    const today = new Date();
    const scheduleDate = new Date(bookingDate);
    let diffBusinessDays = 0;
    let current = new Date(today);

    while (current < scheduleDate) {
        current.setDate(current.getDate() + 1);
        const day = current.getDay();
        if (day !== 0 && day !== 6) {
            diffBusinessDays++;
        }
    }

    if( bookingFlag === null && diffBusinessDays  > 5){
        return true;
    } else if (bookingFlag === false &&  diffBusinessDays  > 2 ) {
        return true;
    }
    return false;
}

// cafeteria Usage Form
function handleBookingClick(button) {

    const scheduleId = button.getAttribute("data-id");
    const date = button.getAttribute("data-date");
    const start = button.getAttribute("data-start");
    const end = button.getAttribute("data-end");

    if (!validateBooking(scheduleId)) {

        const applicantName = document.getElementById('applicantName');
        const applicantStaffId = document.getElementById('applicantStaffId');
        const team = document.getElementById('team');
        const department = document.getElementById('department');
        const preferredDate = document.getElementById('preferredDate');

        applicantName.value = currentStaffName;
        applicantStaffId.value = currentStaffNo;
        team.value = currentTeam;
        department.value = currentDepartment;
        preferredDate.value = `${date} (${start} - ${end})`;

        const modal = new bootstrap.Modal(document.getElementById('scheduleModal'));
        modal.show();
    } else {
        alertAction("This time slot is already booked. Please choose another one.", {
            title: "Booking Conflict!", variant: "danger"
        });
    }
}

function validateBooking(scheduleId) {
    fetch(`${checkBooked}`, {
        method: 'POST',
        body: new URLSearchParams({
            scheduleId: scheduleId,
        })
    })
    .then(res =>  res.json())
    .then(isConflict => {
        console.log("is Conflict - ", isConflict);
        return isConflict;
    })
    .catch(error => console.error("Error:", error));
}

// cafeteria Usage Form


