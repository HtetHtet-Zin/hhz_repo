const tableBody = document.getElementById("staffTableBody");
const pagination = document.getElementById("pagination");
const searchInput = document.getElementById("search");
const countElem = document.getElementById('count');
const participantElem = document.getElementById('participant-text');

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
    tableBody.innerHTML = "";

    if (!data.content || data.content.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="${thCount}" style="text-align:center;">No participants yet</td>
            </tr>
        `;
        return;
    }

    data.content.forEach((booking, index) => {
    const isPending = booking.status === 'Pending';
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
                 <td>${booking.attendees}</td>
                <td>${booking.accessories}</td>
                 <td style="text-align:center;">
                    <button type="button" id="approveButton" onclick="approveModalOpen(this)"
                                       class="btn confirm"
                                           value="${booking.id}" data-id="${booking.scheduleId}"  ${!isPending ? 'disabled' : ''}>
                                     <i class="bi bi-patch-exclamation-fill"></i>
                                     </button>
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



// --- Enable/Disable Approve/Reject Buttons Based on Textarea ---
document.getElementById("modalTextarea").addEventListener("input", function () {
  const reason = this.value.trim();
  const approveBtn = document.querySelector('#customAlertBox button[onclick="approveModalSubmit()"]');
  const rejectBtn = document.querySelector('#customAlertBox button[onclick="rejectModalSubmit()"]');

  const enable = reason.length > 0;

  if (approveBtn) {
    approveBtn.disabled = !enable;
    approveBtn.classList.toggle("opacity-50", !enable);
  }

  if (rejectBtn) {
    rejectBtn.disabled = !enable;
    rejectBtn.classList.toggle("opacity-50", !enable);
  }
});

// --- Approve Modal Open ---
function approveModalOpen(button) {

  document.getElementById("customAlertBox").style.display = "block";
    const selectedId = button.value.trim();
    const scheduleId = button.getAttribute("data-id");
   document.getElementById("selectedId").value = selectedId;
   document.getElementById("scheduleId").value = scheduleId;
}

// --- Close Modal & Reset ---
function approveModalClose() {
  document.getElementById("customAlertBox").style.display = "none";
  document.getElementById("modalTextarea").value = "";

  const approveBtn = document.querySelector('#customAlertBox button[onclick="approveModalSubmit()"]');
  const rejectBtn = document.querySelector('#customAlertBox button[onclick="rejectModalSubmit()"]');

  if (approveBtn) {
    approveBtn.disabled = true;
    approveBtn.classList.add("opacity-50");
  }

  if (rejectBtn) {
    rejectBtn.disabled = true;
    rejectBtn.classList.add("opacity-50");
  }
}

// --- Submit Approve ---
function approveModalSubmit() {
  const reason = document.getElementById("modalTextarea").value.trim();
  const selectedId =document.getElementById("selectedId").value;
  const scheduleId =document.getElementById("scheduleId").value;

  if (reason === "") {
     alertAction("Please enter a reason.", {
                      title: "Missing Reason!", variant: "danger"
                  });
    return;
  }
   const formData = new FormData();

    if (validateBooking(scheduleId)) {
            approveModalClose();
              alertAction("This time slot is already booked.", {
                  title: "Booking Conflict!", variant: "danger"
              });

   } else {
       formData.append("bookingId", selectedId);
       formData.append("approveReason", reason);
       formData.append("formAction", "approve");


     const okButton = document.querySelector('#customAlertBox button[onclick="approveModalSubmit()"]');
     if (okButton) okButton.disabled = true;

             fetch('/club/approve', {
                 method: 'POST',
                 body: formData
             })
            .then(response =>  response.json())
            .then(data => {
                const redirectUrl = new URL(data.redirectUrl, window.location.origin);
                redirectUrl.searchParams.append('message', data.message || '');
                redirectUrl.searchParams.append('messageType', data.status || '');
                window.location.href = redirectUrl;
            })
            .catch(error => console.error("Error:", error));

     approveModalClose();

          }
}

// --- Submit Reject ---
function rejectModalSubmit() {
  const reason = document.getElementById("modalTextarea").value.trim();
  const selectedId = document.getElementById("approveButton").value.trim();


  if (reason === "") {
    approveModalShowMessage("Please enter a reason.", "error");
    return;
  }

  if (selectedId === "") {
    approveModalShowMessage("No booking selected.", "error");
    approveModalClose();
    return;
  }

    const formData = new FormData();
    formData.append("bookingId", selectedId);
    formData.append("approveReason", reason);
    formData.append("formAction", "reject");

  const rejectBtn = document.querySelector('#customAlertBox button[onclick="rejectModalSubmit()"]');
  if (rejectBtn) rejectBtn.disabled = true;

            fetch('/club/approve', {
                method: 'POST',
                body: formData
            })
           .then(response =>  response.json())
           .then(data => {
               const redirectUrl = new URL(data.redirectUrl, window.location.origin);
               redirectUrl.searchParams.append('message', data.message || '');
               redirectUrl.searchParams.append('messageType', data.status || '');
               window.location.href = redirectUrl;
           })
           .catch(error => console.error("Error:", error));


  approveModalClose();
}


    function validateInput(textarea) {
        const value = textarea.value;
        const length = value.length;

        const counter = document.getElementById("wordCounter");
        if (counter) {
            counter.textContent = length;
        }

        if (length > 255) {
            textarea.setCustomValidity("Maximum 255 characters allowed.");
        } else {
            textarea.setCustomValidity("");
        }

        textarea.reportValidity(); // Optional: show native validation message
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
            console.log("is Conflict - ", isConflict);
            return isConflict;
        })
        .catch(error => console.error("Error:", error));

    }