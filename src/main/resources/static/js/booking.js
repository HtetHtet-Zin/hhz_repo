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
                    <button type="button" id="approveButton" onclick="approveModalOpen()"
                                       class="w-[120px] h-[45px] bg-[#023047] text-white text-sm font-medium rounded-[10px] shadow-md hover:bg-[#0097b2]"
                                           data-id="${booking.id}"  >
                                       <i class="fa-solid fa-sliders"></i> Action
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
function approveModalOpen() {

  document.getElementById("customAlertBox").style.display = "block";
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
  const selectedIds = Array.from(document.querySelectorAll('.rowCheckbox:checked'))
      .map(cb => cb.getAttribute('data-form-id'));

  if (reason === "") {
    approveModalShowMessage("Please enter a reason.", "error");
    return;
  }

  if (selectedIds.length === 0) {
    approveModalShowMessage("No forms selected.", "error");
    approveModalClose();
    return;
  }

  document.getElementById("formIds").value = selectedIds.join(",");
  document.getElementById("approveReason").value = reason;
  document.getElementById("formAction").value = "approve";


  const okButton = document.querySelector('#customAlertBox button[onclick="approveModalSubmit()"]');
  if (okButton) okButton.disabled = true;

  document.getElementById("approveForm").submit();
  approveModalClose();
}

// --- Submit Reject ---
function rejectModalSubmit() {
  const reason = document.getElementById("modalTextarea").value.trim();
  const selectedIds = Array.from(document.querySelectorAll('.rowCheckbox:checked'))
      .map(cb => cb.getAttribute('data-form-id'));

  if (reason === "") {
    approveModalShowMessage("Please enter a reason.", "error");
    return;
  }

  if (selectedIds.length === 0) {
    approveModalShowMessage("No forms selected.", "error");
    approveModalClose();
    return;
  }

  document.getElementById("formIds").value = selectedIds.join(",");
  document.getElementById("approveReason").value = reason;
  document.getElementById("formAction").value = "reject";


  const rejectBtn = document.querySelector('#customAlertBox button[onclick="rejectModalSubmit()"]');
  if (rejectBtn) rejectBtn.disabled = true;

  document.getElementById("approveForm").submit();
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