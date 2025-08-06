const calendarHeader = document.getElementById('calendarHeader');
const calendarBody = document.getElementById('calendarBody');
const daysCount = 7;
const timeSlots = ['09:00-10:00', '10:00-11:00', '11:00-12:00', '12:00-13:00', '13:00-14:00', '14:00-15:00', '15:00-16:00', '16:00-17:00'];

function getMonday(d) {
  const date = new Date(d);
  const day = date.getDay();
  const diff = day === 0 ? -6 : 1 - day;
  date.setDate(date.getDate() + diff);
  return date;
}

let currentWeekStart = getMonday(new Date());

function renderCalendar(weekStart) {
  calendarHeader.innerHTML = '<th class="time-slot">Time / Date</th>';
  calendarBody.innerHTML = '';

  for (let i = 0; i < daysCount; i++) {
    const date = new Date(weekStart);
    date.setDate(weekStart.getDate() + i);
    const dayLabel = date.toLocaleDateString(undefined, { weekday: 'short' });
    const dateLabel = date.toISOString().split('T')[0];
    const th = document.createElement('th');
    th.dataset.date = dateLabel;
    th.innerHTML = `${dayLabel}<br><small>${dateLabel}</small>`;
    calendarHeader.appendChild(th);
  }

  timeSlots.forEach(time => {
    const tr = document.createElement('tr');

    const th = document.createElement('th');
    th.classList.add('time-slot');
    th.textContent = time;
    tr.appendChild(th);

    for (let i = 0; i < daysCount; i++) {
      const date = new Date(weekStart);
      date.setDate(weekStart.getDate() + i);
      const dateISO = date.toISOString().split('T')[0];

      const td = document.createElement('td');
      td.dataset.date = dateISO;
      td.dataset.time = time;
      td.title = `${dateISO} ${time}`;

      td.addEventListener('click', () => {
        td.classList.toggle('selected');
      });

      tr.appendChild(td);
    }

    calendarBody.appendChild(tr);
  });
}

document.getElementById('prevWeekBtn').addEventListener('click', () => {
  currentWeekStart.setDate(currentWeekStart.getDate() - 7);
  renderCalendar(currentWeekStart);
});

document.getElementById('nextWeekBtn').addEventListener('click', () => {
  currentWeekStart.setDate(currentWeekStart.getDate() + 7);
  renderCalendar(currentWeekStart);
});

document.getElementById('eventForm').addEventListener('submit', e => {
  e.preventDefault();
  const eventName = document.getElementById('eventName').value.trim();
  const eventDesc = document.getElementById('eventDesc').value.trim();

  if (!eventName) {
    alert('Please enter an event name.');
    return;
  }

  const selectedCells = [...document.querySelectorAll('td.selected')];
  if (selectedCells.length === 0) {
    alert('Please select at least one date/time slot.');
    return;
  }

  const eventMap = new Map();

  selectedCells.forEach(cell => {
    const date = cell.dataset.date;
    const time = cell.dataset.time;
    if (!eventMap.has(eventName)) {
      eventMap.set(eventName, {
        descriptionSet: new Set([eventDesc]),
        dateTimeSet: new Set([`${date} ${time}`])
      });
    } else {
      const event = eventMap.get(eventName);
      event.descriptionSet.add(eventDesc);
      event.dateTimeSet.add(`${date} ${time}`);
    }
  });

  console.log('Events to create:', eventMap);

  e.target.reset();
  selectedCells.forEach(cell => cell.classList.remove('selected'));
});

renderCalendar(currentWeekStart);
