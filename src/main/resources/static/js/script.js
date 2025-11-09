document.addEventListener("DOMContentLoaded", () => {
    console.log("RoomieRadar Frontend Loaded");

    const navLinks = document.querySelectorAll(".navbar .nav-link");
    const currentPage = window.location.pathname.split("/").pop();

    navLinks.forEach(link => {
        if (link.getAttribute("href") === currentPage) {
            link.classList.add("text-primary");
        } else {
            link.classList.remove("text-primary");
            link.classList.add("text-secondary");
        }
    });

    //  Placeholder Data
    const events = [
        { name: "Laundry Day", date: "2025-11-10", participants: "Alice, Bob" },
        { name: "Rent Payment", date: "2025-11-01", participants: "Alice, Bob, Charlie" }
    ];

    const bills = [
        { name: "Electricity", amount: "$50", due: "2025-11-10", participants: "Alice, Bob", status: "Unpaid" },
        { name: "Internet", amount: "$30", due: "2025-11-12", participants: "Alice, Bob", status: "Paid" }
    ];

    const chores = [
        { name: "Vacuum", assigned: "Alice", due: "2025-11-10", status: "Pending" },
        { name: "Trash", assigned: "Bob", due: "2025-11-09", status: "Completed" }
    ];

    const eventsTable = document.getElementById("events-table");
    if (eventsTable) {
        eventsTable.innerHTML = events.map(e => `
            <tr>
                <td>${e.name}</td>
                <td>${e.date}</td>
                <td>${e.participants}</td>
                <td>
                    <a href="event-details.html" class="btn btn-sm btn-info me-1">View</a>
                    <a href="edit-event.html" class="btn btn-sm btn-warning">Edit</a>
                </td>
            </tr>
        `).join("");
    }

    const billsTable = document.getElementById("bills-table");
    if (billsTable) {
        billsTable.innerHTML = bills.map(b => `
            <tr>
                <td>${b.name}</td>
                <td>${b.amount}</td>
                <td>${b.due}</td>
                <td>${b.participants}</td>
                <td>${b.status}</td>
                <td>
                    <button class="btn btn-sm btn-warning">Edit</button>
                    <button class="btn btn-sm btn-danger">Delete</button>
                </td>
            </tr>
        `).join("");
    }

    const choresTable = document.getElementById("chores-table");
    if (choresTable) {
        choresTable.innerHTML = chores.map(c => `
            <tr>
                <td>${c.name}</td>
                <td>${c.assigned}</td>
                <td>${c.due}</td>
                <td>${c.status}</td>
                <td>
                    <button class="btn btn-sm btn-warning">Edit</button>
                    <button class="btn btn-sm btn-danger">Delete</button>
                </td>
            </tr>
        `).join("");
    }

    document.querySelectorAll("button").forEach(btn => {
        btn.addEventListener("click", e => {
            if (!btn.classList.contains("btn-warning") && !btn.classList.contains("btn-danger")) {
                alert(`${btn.textContent.trim()} placeholder clicked!`);
            }
        });
    });
});
