async function getData() {
    const url = "/processes";
    try {
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`Response status: ${response.status}`);
      }

      const json = await response.json();
      console.log(json);
      return json;
    } catch (error) {
      console.error(error.message);
    }
}

async function fillTable() {
  let items = await getData();

  const table = document.getElementById("testBody");
  items.forEach( item => {
    let row = table.insertRow();
    let date = row.insertCell(0);
    date.innerHTML = item.pid;
    let name = row.insertCell(1);
    name.innerHTML = item.command;
  });
}

window.addEventListener("DOMContentLoaded", fillTable);




