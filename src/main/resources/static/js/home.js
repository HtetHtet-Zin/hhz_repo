
    const daysContainer = document.querySelector(".days");
    const monthYear = document.getElementById("monthYear");
    const prevBtn = document.getElementById("prev");
    const nextBtn = document.getElementById("next");
    const eventList = document.getElementById("eventList");
//    const selectedDateText = document.getElementById("selectedDateText");
//    const openAddEventBtn = document.getElementById("openAddEventBtn");
    const eventModal = document.getElementById("eventModal");
    const closeModalBtn = document.getElementById("closeModalBtn");
    const eventForm = document.getElementById("eventForm");
    const addTimeSlotBtn = document.getElementById("addTimeSlotBtn");
    const timeSlotsWrapper = document.getElementById("timeSlotsWrapper");
    const eventPhoto = document.getElementById("eventPhoto");
    const uploadProgress = document.querySelector("#uploadProgress .progress");
    const themeToggle = document.getElementById("themeToggle");
    const searchEvent = document.getElementById("searchEvent");

    const steps = document.querySelectorAll(".form-step");
    const nextStepBtn = document.querySelectorAll(".next-btn");
    const prevStepBtn = document.querySelectorAll(".prev-btn");

    const monthNames = ["January","February","March","April","May","June","July","August","September","October","November","December"];
    let currentMonth = new Date().getMonth();
    let currentYear = new Date().getFullYear();
    let selectedDate = null;
    let events = JSON.parse(localStorage.getItem("events")) || {};
    let photoBase64 = null;

    // Show step function
    function showStep(step) {
      steps.forEach(s => s.classList.remove("active"));
      steps[step - 1].classList.add("active");
    }
    showStep(1);

    // Calendar Rendering
    function renderCalendar() {
      const firstDay = new Date(currentYear, currentMonth, 1);
      const lastDay = new Date(currentYear, currentMonth + 1, 0);
      monthYear.textContent = `${monthNames[currentMonth]} ${currentYear}`;
      daysContainer.innerHTML = "";

      for (let i = 0; i < firstDay.getDay(); i++) {
        daysContainer.innerHTML += `<div></div>`;
      }

      for (let day = 1; day <= lastDay.getDate(); day++) {
        const dayElement = document.createElement("div");
        dayElement.textContent = day;
        const today = new Date();
        if (day === today.getDate() && currentMonth === today.getMonth() && currentYear === today.getFullYear()) {
          dayElement.classList.add("today");
        }

        const key = `${day}-${currentMonth + 1}-${currentYear}`;
        if (events[key]) {
          const countBadge = document.createElement("span");
          countBadge.classList.add("event-count");
          countBadge.textContent = events[key].length;
          dayElement.appendChild(countBadge);
        }

        dayElement.addEventListener("click", () => {
          selectedDate = key;
//          selectedDateText.textContent = selectedDate;
//          openAddEventBtn.disabled = false;
          displayEvents();
        });

        daysContainer.appendChild(dayElement);
      }
    }

    function displayEvents() {
      eventList.innerHTML = "";
      if (!selectedDate) return;
      if (events[selectedDate] && events[selectedDate].length > 0) {
        events[selectedDate]
          .filter(e => e.name.toLowerCase().includes(searchEvent.value.toLowerCase()))
          .forEach((event, index) => {
            const li = document.createElement("li");
            li.style.borderLeft = `8px solid ${event.color}`;
            li.innerHTML = `
              ${event.photo ? `<img src="${event.photo}" class="event-photo" alt="photo" />` : ''}
              <div>
                <strong>${event.name}</strong><br>
                <em>${event.summary}</em><br>
                <p>${event.desc}</p>
                <div><strong>Time:</strong> ${event.timeSlots.map(t => `${t.from} - ${t.to}`).join(", ")}</div>
              </div>
              <div class="event-actions">
                <button onclick="editEvent(${index})" aria-label="Edit event">✏️</button>
                <button onclick="deleteEvent(${index})" aria-label="Delete event">🗑️</button>
              </div>
            `;
            eventList.appendChild(li);
          });
      } else {
        eventList.innerHTML = `<li>No events for this day</li>`;
      }
    }

    // Open & Close Modal
    function openModal() {
      eventModal.setAttribute("aria-hidden", "false");
      showStep(1);
    }
    function closeModal() {
      eventModal.setAttribute("aria-hidden", "true");
      eventForm.reset();
      photoBase64 = null;
      timeSlotsWrapper.innerHTML = `
        <label>Time Slots</label>
        <div class="time-slot">
          <input type="time" class="eventFromTime" required />
          <input type="time" class="eventToTime" required />
        </div>
      `;
      uploadProgress.style.width = "0%";
      document.getElementById("editIndex").value = "";
    }

    // Step Navigation
    nextStepBtn.forEach(btn => {
      btn.addEventListener("click", () => {
        if (!document.getElementById("eventName").value.trim() ||
            !document.getElementById("eventDesc").value.trim() ||
            !document.getElementById("eventSummary").value.trim()) {
          alert("Please fill out all fields before continuing.");
          return;
        }
        showStep(2);
      });
    });
    prevStepBtn.forEach(btn => {
      btn.addEventListener("click", () => showStep(1));
    });

    // Add Event
    function addEvent(e) {
      e.preventDefault();
      const name = document.getElementById("eventName").value;
      const desc = document.getElementById("eventDesc").value;
      const summary = document.getElementById("eventSummary").value;
      const color = document.getElementById("eventColor").value;

      const timeSlots = [...document.querySelectorAll(".time-slot")].map(slot => ({
        from: slot.querySelector(".eventFromTime").value,
        to: slot.querySelector(".eventToTime").value
      }));

      if (!events[selectedDate]) events[selectedDate] = [];
      const editIndex = document.getElementById("editIndex").value;
      if (editIndex !== "") {
        events[selectedDate][editIndex] = { name, desc, summary, color, timeSlots, photo: photoBase64 || events[selectedDate][editIndex].photo };
      } else {
        events[selectedDate].push({ name, desc, summary, color, timeSlots, photo: photoBase64 });
      }

      localStorage.setItem("events", JSON.stringify(events));
      displayEvents();
      renderCalendar();
      closeModal();
    }

    // Edit Event
    window.editEvent = function(index) {
      const event = events[selectedDate][index];
      openModal();
      document.getElementById("modalTitle").textContent = "Edit Event";
      document.getElementById("eventName").value = event.name;
      document.getElementById("eventDesc").value = event.desc;
      document.getElementById("eventSummary").value = event.summary;
      document.getElementById("eventColor").value = event.color;
      document.getElementById("editIndex").value = index;

      timeSlotsWrapper.innerHTML = `<label>Time Slots</label>`;
      event.timeSlots.forEach(slot => {
        const div = document.createElement("div");
        div.classList.add("time-slot");
        div.innerHTML = `<input type="time" class="eventFromTime" value="${slot.from}" required />
                         <input type="time" class="eventToTime" value="${slot.to}" required />`;
        timeSlotsWrapper.appendChild(div);
      });
    }

    // Delete Event
    window.deleteEvent = function(index) {
      if (confirm("Are you sure you want to delete this event?")) {
        events[selectedDate].splice(index, 1);
        if (events[selectedDate].length === 0) delete events[selectedDate];
        localStorage.setItem("events", JSON.stringify(events));
        displayEvents();
        renderCalendar();
      }
    }

    // Extra Features
    addTimeSlotBtn.addEventListener("click", () => {
      const slot = document.createElement("div");
      slot.classList.add("time-slot");
      slot.innerHTML = `<input type="time" class="eventFromTime" required />
                        <input type="time" class="eventToTime" required />`;
      timeSlotsWrapper.appendChild(slot);
    });

    eventPhoto.addEventListener("change", () => {
      const file = eventPhoto.files[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = () => { photoBase64 = reader.result; };
      reader.readAsDataURL(file);

      let progress = 0;
      const interval = setInterval(() => {
        if (progress >= 100) clearInterval(interval);
        else {
          progress += 10;
          uploadProgress.style.width = progress + "%";
        }
      }, 100);
    });

    themeToggle.addEventListener("click", () => document.body.classList.toggle("dark"));
//    openAddEventBtn.addEventListener("click", openModal);
    closeModalBtn.addEventListener("click", closeModal);
    eventForm.addEventListener("submit", addEvent);
    searchEvent.addEventListener("input", displayEvents);

    renderCalendar();
    prevBtn.addEventListener("click", () => {
      currentMonth--;
      if (currentMonth < 0) {
        currentMonth = 11;
        currentYear--;
      }
      renderCalendar();
    });
    nextBtn.addEventListener("click", () => {
      currentMonth++;
      if (currentMonth > 11) {
        currentMonth = 0;
        currentYear++;
      }
      renderCalendar();
    });
