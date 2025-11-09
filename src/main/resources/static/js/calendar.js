document.addEventListener("DOMContentLoaded", () => {

    const upcomingList = document.getElementById("calendar-upcoming");
    const monthTableBody = document.getElementById("calendar-month-body");

    if (!upcomingList || !monthTableBody) return;

    const fmtDate = (iso) => {
        const d = new Date(iso);
        if (Number.isNaN(d.getTime())) return iso;
        return d.toLocaleString();
    };


    const now = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1).toISOString();
    const monthEnd   = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59, 999).toISOString();

    //For later household selection, sets it as a data-attr on the container
    const container = document.getElementById("calendar-root");
    const householdId = container?.dataset?.householdId; // e.g., set th:attr="data-household-id=${householdId}"

    const buildRangeUrl = () => {
        const u = new URL("/api/calendaritems/range", window.location.origin);
        u.searchParams.set("start", monthStart);
        u.searchParams.set("end", monthEnd);
        if (householdId) u.searchParams.set("householdId", householdId);
        return u.toString();
    };

    const fetchCalendarItems = async () => {
        try {
            const res = await fetch(buildRangeUrl(), { credentials: "same-origin" });
            if (res.ok) return await res.json();
            // fallback
            const resAll = await fetch("/api/calendaritems", { credentials: "same-origin" });
            return resAll.ok ? await resAll.json() : [];
        } catch {
            return [];
        }
    };

    const render = (items) => {
        // Sort by start date
        items.sort((a, b) => new Date(a.dateStart) - new Date(b.dateStart));

        // Upcoming list (next 5)
        const nowTs = Date.now();
        const upcoming = items.filter(i => new Date(i.dateStart).getTime() >= nowTs).slice(0, 5);

        upcomingList.innerHTML = upcoming.length
            ? upcoming.map(i => `
          <li class="list-group-item d-flex justify-content-between align-items-start">
            <div>
              <div class="fw-semibold">${i.name ?? "(Untitled)"}</div>
              <small class="text-muted">${fmtDate(i.dateStart)}${i.dateEnd ? " – " + fmtDate(i.dateEnd) : ""}</small><br/>
              ${i.description ? `<small>${i.description}</small>` : ""}
            </div>
            <span class="badge bg-secondary ms-2">#${i.id}</span>
          </li>
        `).join("")
            : `<li class="list-group-item text-muted">No upcoming items.</li>`;

        //This month table
        monthTableBody.innerHTML = items.length
            ? items.map(i => `
          <tr>
            <td>${i.name ?? "(Untitled)"}</td>
            <td>${fmtDate(i.dateStart)}</td>
            <td>${i.dateEnd ? fmtDate(i.dateEnd) : "-"}</td>
            <td>${i.repeatDuration ?? "-"}</td>
            <td>
              <a href="/events" class="btn btn-sm btn-info me-1">View</a>
              <a href="/events" class="btn btn-sm btn-warning me-1">Edit</a>
              <button class="btn btn-sm btn-danger" data-id="${i.id}">Delete</button>
            </td>
          </tr>
        `).join("")
            : `<tr><td colspan="5" class="text-muted">No items for this month.</td></tr>`;

        //Hook delete buttons (simple demo – keep using your existing delete API)
        monthTableBody.querySelectorAll('button.btn-danger[data-id]').forEach(btn => {
            btn.addEventListener('click', async () => {
                const id = btn.getAttribute('data-id');
                if (!confirm('Delete this calendar item?')) return;
                const resp = await fetch(`/api/calendaritems/${id}`, { method: 'DELETE', credentials: "same-origin" });
                if (resp.status === 204) {
                    btn.closest('tr')?.remove();
                } else {
                    alert('Failed to delete.');
                }
            });
        });
    };

    fetchCalendarItems().then(render);
});
