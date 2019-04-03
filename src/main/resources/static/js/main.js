'use strict';

document.body.onload = main;

let latest = '';
let db = [];
let patch = [];

function updatePatches(event) {
    event.preventDefault();
    latest = event.target.id;

    let dbCol = prepareColumn('db');
    dbCol.querySelectorAll('.btn').forEach(b => b.disabled = true);

    let dbColOld = document.querySelector('#db');

    dbColOld.replaceWith(dbCol);

    let patchColOld = document.querySelector('#patch');
    patchColOld.innerHTML = '';
    patch = [];

    fetch('/api/list?db=' + latest, {
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        method: 'POST',
    })
        .then(r => r.json())
        .then(d => {
            patch = d.list;
            let patchCol = prepareColumn('patch');
            patchColOld.innerHTML = patchCol.innerHTML;
            document.querySelectorAll('.btn').forEach(b => b.disabled = false);
        })
        .catch(alert);
}

function prepareColumn(type) {
    let ul = document.createElement('ul');
    ul.classList.add('list-group');

    switch (type) {
        case 'db':
            let min = Math.min(...db.map(s => parseInt(s, 10)));
            db.forEach(i => {
                let a = document.createElement('a');
                a.innerHTML = i;
                a.href = '/api/getdb/' + i;
                if (i === latest)
                    a.style.color = 'red';
                let li = document.createElement('li');
                li.classList.add('list-group-item', 'd-flex', 'justify-content-between', 'align-items-center');
                li.appendChild(a);
                if (i !== latest && parseInt(i, 10) !== min) {
                    let button = document.createElement('button');
                    button.id = i;
                    button.type = 'submit';
                    button.classList.add('btn', 'btn-primary', 'tn-lg', 'active');
                    button.innerText = 'use';
                    button.addEventListener('click', updatePatches);
                    li.appendChild(button);
                }
                ul.appendChild(li);
            });
            break;
        case 'patch':
            patch.forEach(i => {
                let a = document.createElement('a');
                a.innerHTML = i;
                a.href = '/api/download/' + i;
                let li = document.createElement('li');
                li.classList.add('list-group-item');
                li.appendChild(a);
                ul.appendChild(li);
            });
            break;
    }
    let p = document.createElement('p');
    p.classList.add('font-weight-bold');
    p.innerHTML = type.toUpperCase() + '\'s:';
    let col = document.createElement('div');
    col.classList.add('col');
    col.id = type;
    col.appendChild(p);
    col.appendChild(ul);

    return col;
}

async function fetchData(type) {
    const res = await fetch('/api/list?type=' + type, {
        headers: {'Content-Type': 'application/json; charset=utf-8'},
        method: 'GET',
    })
        .then(r => r.json())
        .catch(e => console.error(e));
    switch (type) {
        case 'db':
            db = res.list;
            latest = res.latest;
            break;
        case 'patch':
            patch = res.list;
            break;
    }
}

async function main() {
    await fetchData("db");
    let dbCol = prepareColumn('db');
    let row = document.createElement('div');
    row.classList.add('row');
    row.appendChild(dbCol);
    row.querySelectorAll('.btn').forEach(b => b.disabled = true);
    let container = document.createElement('div');
    container.classList.add('container');
    container.appendChild(row);
    document.body.appendChild(container);
    await fetchData("patch").then(() => {
        let patchCol = prepareColumn('patch');
        document.querySelector('.row').appendChild(patchCol);
        document.querySelectorAll('.btn').forEach(b => b.disabled = false);
    });
}
