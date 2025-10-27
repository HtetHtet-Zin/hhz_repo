const tableBody = document.getElementById("scheduleTableBody");
const pagination = document.getElementById("pagination");
const searchInput = document.getElementById("search");
const eventId = document.getElementById("eventId").value;
const joinBtn = document.getElementById("joinBtn");
const selectAllCheckbox = document.getElementById("checkSelectAll");
const countElem = document.getElementById('count');
const activityElem = document.getElementById('activity-text');
let currentPage = 0;
let currentKeyword = "";

const table = tableBody.closest('table');
const thCount = table.querySelectorAll('th').length;

function toggleAll(source) {
    isSelectAll = source.checked;

    selectedScheduleIds.clear();
    if (source.checked) {
        // Add all visible checkbox IDs to the set
        document.querySelectorAll('input[name="selectedScheduleIds"]').forEach(cb => {
            cb.checked = true;
        });
        selectedScheduleIds = new Set(allScheduleIds);
    } else {
        // Uncheck all visible checkboxes
        document.querySelectorAll('input[name="selectedScheduleIds"]').forEach(cb => {
            cb.checked = false;
        });
        selectedScheduleIds = new Set();
    }

    checkJoinButtonState();
}

function checkJoinButtonState() {
    const isSame =
        originalScheduleIds.size === selectedScheduleIds.size &&
        [...originalScheduleIds].every(id => selectedScheduleIds.has(id));
    joinBtn.disabled = isSame;
    if(allScheduleIds.length == selectedScheduleIds.size) {
        selectAllCheckbox.checked = true;
    }
}

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
        countElem.textContent = "no";``
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

let isSelectAll = false;

function renderTable(data) {
    const count = data.page.totalElements;
    tableBody.innerHTML = "";
    if (!data.content || data.content.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="${thCount}" style="text-align:center;">No schedules yet</td>
            </tr>
        `;
        selectAllCheckbox.disabled = true;
        joinBtn.disabled = true;
        return;
    }

    selectAllCheckbox.disabled = false;

    data.content.forEach((schedule, index) => {
        const id = Number(schedule.id);
        const isChecked = isSelectAll || selectedScheduleIds.has(id);
        tableBody.innerHTML += `
            <tr>
                <td>
                    <div>
                        <input
                            class="schedule-checkbox"
                            type="checkbox"
                            name="selectedScheduleIds"
                            value="${id}"
                            ${isChecked ? "checked" : ""}
                        />
                    </div>
                </td>
                <td>${(data.page.number * data.page.size) + index + 1}</td>
                <td>${schedule.name}</td>
                <td>${schedule.date} (${schedule.fromTime} - ${schedule.toTime})</td>
                <td>${schedule.eventDto.location}</td>
                <td>${schedule.participantCount == 0 ? 'No Participant' : (schedule.participantCount == 1 ? '1 participant' : schedule.participantCount + ' participants')}</td>
            </tr>
        `;
    });

    document.querySelectorAll('input.schedule-checkbox').forEach(cb => {
        cb.addEventListener('change', e => {
            const id = Number(e.target.value);
            if (e.target.checked) {
                selectedScheduleIds.add(id);
            } else {
                selectedScheduleIds.delete(id);
                isSelectAll = false;
                selectAllCheckbox.checked = false;
            }
            checkJoinButtonState();
        });
    });
    checkJoinButtonState();
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

document.getElementById("joinBtn").addEventListener("click", () => {
    const eventName = document.getElementById("eventName").textContent;
    const params = new URLSearchParams();
    selectedScheduleIds.forEach(id => params.append("registeredScheduleIds", id));
    params.append("eventId", eventId);
    params.append("eventName", eventName);
    params.append("isNew", isNew);

    fetch(`${registrationUrl}`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: params.toString()
    })
    .then(res => res.json())
    .then(data => {
        const redirectUrl = new URL(data.redirectUrl, window.location.origin);
        redirectUrl.searchParams.append('message', data.message || '');
        redirectUrl.searchParams.append('messageType', data.status || '');
        window.location.href = redirectUrl;
    })
    .catch(err => {
         console.error("Error :", err)
    });
});

