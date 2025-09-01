const tableBody = document.getElementById("scheduleTableBody");
const pagination = document.getElementById("pagination");
const searchInput = document.getElementById("search");
const eventId = document.getElementById("eventId").value;
const eventName = document.getElementById("eventName").value;
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
        } else if (schedule.bookingFlag === false) {
            if(handleBookingDate(schedule.date, schedule.bookingFlag)){
             btnHtml = `
                <button class="book-btn"
                        data-id="${schedule.id}"
                        data-date="${schedule.date}"
                        data-start="${schedule.fromTime}"
                        data-end="${schedule.toTime}"
                        onclick="handleEditBookingClick(this)">
                     <i class="bi bi-hourglass-split"></i>
                </button>
                `;
            }else{
             btnHtml = `
                <button class="book-btn"
                        data-id="${schedule.id}"
                        data-date="${schedule.date}"
                        data-start="${schedule.fromTime}"
                        data-end="${schedule.toTime}"
                        onclick="handleEditBookingClick(this)">
                    <i class="bi bi-hourglass-split"></i>
                </button>
                `;
            }
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

function handleBookingDate(bookingDate, bookingFlag){
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

    if( bookingFlag === null && diffBusinessDays  >= 5){
        return true;
    } else if (bookingFlag === false &&  diffBusinessDays  > 2 ) {
        return true;
    }
    return false;
}

// cafeteria Usage New Form
const applicantName = document.getElementById('applicantName');
const applicantStaffId = document.getElementById('applicantStaffId');
const team = document.getElementById('team');
const department = document.getElementById('department');
const preferredDate = document.getElementById('preferredDate');
const scheduleId = document.getElementById('scheduleId');
const accessoriesSelect = document.getElementById("accessories");
const selectedAccessoriesDiv = document.getElementById("selectedAccessories");
const attendees = document.getElementById("attendees");
const purpose = document.getElementById("purpose");
const signature = document.getElementById("signature");
const preview = document.getElementById("signaturePreview");
const uploadLink = document.getElementById("uploadSignatureLink");

// cafeteria Usage Edit Form
const editApplicantName = document.getElementById('editApplicantName');
const editApplicantStaffId = document.getElementById('editApplicantStaffId');
const editTeam = document.getElementById('editTeam');
const editDepartment = document.getElementById('editDepartment');
const editPreferredDate = document.getElementById('editPreferredDate');
const editScheduleId = document.getElementById('editScheduleId');
const editAccessoriesSelect = document.getElementById("editAccessories");
const editSelectedAccessoriesDiv = document.getElementById("editSelectedAccessories");
const editAttendees = document.getElementById("editAttendees");
const editPurpose = document.getElementById("editPurpose");
const editSignature = document.getElementById("editSignature");
const editPreview = document.getElementById("editSignaturePreview");
const editUploadLink = document.getElementById("editUploadSignatureLink");

let selectedAccessories = new Set();
let start;
let end;
let date;

// cafeteria Usage Form
function handleBookingClick(button) {

    const id = button.getAttribute("data-id");
    date = button.getAttribute("data-date");
    start = button.getAttribute("data-start");
    end = button.getAttribute("data-end");

    if (validateBooking(id)) {

        scheduleId.value =  id;
        applicantName.value = currentStaffName;
        applicantStaffId.value = currentStaffNo;
        team.value = currentTeam;
        department.value = currentDepartment;
        preferredDate.value = `${date} (${start} - ${end})`;

        checkDuration(start, end, date, "dateError");
        setToday();
        const modal = new bootstrap.Modal(document.getElementById('scheduleModal'));
        modal.show();
    } else {
        alertAction("This time slot is already booked. Please choose another one.", {
            title: "Booking Conflict!", variant: "danger"
        });
    }
}

function handleEditBookingClick(button) {
    const id = button.getAttribute("data-id");
    const date = button.getAttribute("data-date");
    start = button.getAttribute("data-start");
    end = button.getAttribute("data-end");

    editScheduleId.value =  id;
    editPreferredDate.value = `${date} (${start} - ${end})`;

    const modal = new bootstrap.Modal(document.getElementById('editScheduleModal'));
    modal.show();
}

function validateBooking(scheduleId) {
    return   fetch(`${checkBooked}`, {
        method: 'POST',
        body: new URLSearchParams({
            scheduleId: scheduleId,
        })
    })
    .then(res =>  res.json())
    .then(isConflict => {
        return isConflict;
    })
    .catch(error => console.error("Error:", error));
}

function setToday() {
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('signedDate').value = today;
}

accessoriesSelect.addEventListener("change", function () {
    const selectedValue = this.value;
    const selectedText = this.options[this.selectedIndex].text;

    if (selectedValue && !selectedAccessories.has(selectedValue)) {
        selectedAccessories.add(selectedValue);

        const col = document.createElement("div");
        col.className = "col-4";
        col.dataset.value = selectedValue;

        // Create card-style chip
        col.innerHTML = `
            <div class="border rounded p-2 d-flex justify-content-between align-items-center">
                <span>${selectedText}</span>
                <button type="button" class="btn-close ms-2" aria-label="Remove"></button>
            </div>
        `;

        // Handle remove
        col.querySelector(".btn-close").addEventListener("click", function () {
            selectedAccessories.delete(selectedValue);
            col.remove();
        });

        selectedAccessoriesDiv.appendChild(col);
    }

    this.value = "";
});

signature.addEventListener("change", function (event) {
    const file = event.target.files[0];

    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
            preview.style.display = "block";
        };
        reader.readAsDataURL(file);
    } else {
        preview.style.display = "none";
        preview.src = "";
    }
});

uploadLink.addEventListener("click", function (e) {
    e.preventDefault();
    signature.click();
});

// Convert time string to minutes since midnight
function timeToMinutes(timeStr) {
    let [time, modifier] = timeStr.split(" ");
    let [hours, minutes] = time.split(":").map(Number);

    if (modifier) {
        if (modifier.toUpperCase() === "PM" && hours !== 12) hours += 12;
        if (modifier.toUpperCase() === "AM" && hours === 12) hours = 0;
    }
    return hours * 60 + minutes;
}

// duration over 5 hours or not
function checkDuration(startStr, endStr, dateStr, errorDivId, maxHours = 5) {
    const errorDiv = document.getElementById(errorDivId);

    let startMinutes = timeToMinutes(startStr);
    let endMinutes = timeToMinutes(endStr);

    const date = new Date(dateStr);
    const day = date.getDay(); // eg. 0 = Sunday, 1 = Monday, 6 = Saturday
    const isWeekend = day === 0 || day === 6;

    const workStart = 9 * 60; // 9:00 AM
    const workEnd = 19 * 60; // 7:00 PM
    const lunchStart = 11 * 60; // 11:00 AM
    const lunchEnd = 13 * 60; // 1:00 PM

    let actualEnd = endMinutes;
    if (endMinutes < startMinutes) actualEnd += 24 * 60;

    const durationMinutes = actualEnd - startMinutes;

    if (durationMinutes > maxHours * 60) {
        errorDiv.innerText = `Duration cannot exceed ${maxHours} hours.`;
        errorDiv.style.display = "block";
        return false;
    }

    if (!isWeekend) {
        if ((startMinutes < lunchEnd && endMinutes > lunchStart)) {
            errorDiv.innerText = "Time cannot overlap lunch break (11:00AM - 1:00PM).";
            errorDiv.style.display = "block";
            return false;
        }
    }

    errorDiv.style.display = "none";
    return true;
}

attendees.addEventListener("input", function() {
    this.value = this.value.replace(/\D/g, '');

    if (this.value.length > 2) {
        this.value = this.value.slice(0, 2);
    }
});

attendees.addEventListener("input", function() {
    if (this.value.trim()) {
        document.getElementById("attendeeError").style.display = "none";
    }
});

purpose.addEventListener("input", function() {
    if (this.value.length <= 255) {
        document.getElementById("purposeError").style.display = "none";
    }
});

signature.addEventListener("change", function() {
    if (this.files.length > 0) {
        document.getElementById("signError").style.display = "none";
    }
});

function validateAll(start, end, date) {
    let isValid = true;

    if (!checkDuration(start, end, date, "dateError")) isValid = false;

    const attendeeError = document.getElementById("attendeeError");
    if (!attendees) {
        attendeeError.textContent = "Expected Attendees Required! (Minimum 5 & Maximum 60)";
        attendeeError.style.display = "block";
        isValid = false;
    } else if (attendees.value < 5 || attendees.value > 60) {
        attendeeError.textContent = "Attendees must be between 5 and 60.";
        attendeeError.style.display = "block";
        isValid = false;
    } else {
        attendeeError.style.display = "none";
    }

    const purposeText = purpose.value.trim();
    const purposeError = document.getElementById("purposeError");
    if (!purposeText) {
        purposeError.textContent = "Purpose of the Event is required!";
        purposeError.style.display = "block";
        isValid = false;
    } else if (purposeText.length > 255) {
        purposeError.textContent = "Purpose must be less than 255 characters!";
        purposeError.style.display = "block";
        isValid = false;
    } else {
        purposeError.style.display = "none";
    }

    const signError = document.getElementById("signError");
    if (!signature.files || signature.files.length === 0) {
        signError.style.display = "block";
        isValid = false;
    } else {
        signError.style.display = "none";
    }

    return isValid;
}

function submitData(submitType) {
    if (!validateAll(start,end)) return;

    const formData = new FormData();
    formData.append("scheduleId", scheduleId.value);
    formData.append("eventId", eventId);
    formData.append("eventName", eventName);
    formData.append("attendees", attendees.value);

    selectedAccessories.forEach(val => formData.append("accessories", val));

    formData.append("purpose", purpose.value);
    formData.append("signature", signature.files[0]);
    formData.append("submitType", submitType);

    fetch(`${bookingUrl}`, {
        method: "POST",
        body: formData
    })
    .then(res => res.json())
    .then(data => {
        const redirectUrl = new URL(data.redirectUrl, window.location.origin);
        redirectUrl.searchParams.append('message', data.message || '');
        redirectUrl.searchParams.append('messageType', data.status || '');
        window.location.href = redirectUrl;
    })
    .catch(err => {
        console.error("Submit error:", err);
    });
}

// Function to reset modal fields
function resetModalFields() {

    attendees.value = "";
    purpose.value = "";
    signature.value = "";

    preview.src = "";
    preview.style.display = "none";

    if (selectedAccessoriesDiv) {
        selectedAccessoriesDiv.innerHTML = "";
    }
    if (typeof selectedAccessories !== "undefined") {
        selectedAccessories.clear();
    }

    if (accessoriesSelect) {
        accessoriesSelect.value = "";
    }

    // Hide all error messages
    document.querySelectorAll(".text-error").forEach(el => el.style.display = "none");
}

// Attach reset function to modal close buttons
document.querySelectorAll("[data-bs-dismiss='modal']").forEach(btn => {
    btn.addEventListener("click", resetModalFields);
});

document.getElementById("saveBtn").addEventListener("click", () => submitData("SAVE"));
document.getElementById("saveContinueBtn").addEventListener("click", () => submitData("CONTINUE"));


// cafeteria Usage Form


