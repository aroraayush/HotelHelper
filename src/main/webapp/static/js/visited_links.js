const clearButton = document.getElementById('clear_links');

if(hotelsArr.length > 0){
    clearButton.style.display = 'block';
    const hotelsParent = document.getElementsByClassName('hotels_parent')[0];

    hotelsArr.forEach(visitObj=> {
        const divOuter = document.createElement("div");
        divOuter.className="col-md-12";
        divOuter.setAttribute('style','margin-top:20px');

        const divInner1 = document.createElement("div");
        divInner1.className="col-md-3";
        divInner1.innerHTML=`<span style="font-weight: bold;font-size: larger;">${visitObj.name === null || visitObj.name.length === 0 ? "N/A" : visitObj.name}</span>`;

        const divInner2 = document.createElement("div");
        divInner2.className="col-md-6";
        divInner2.innerHTML=`<a role=button target="_blank" href="${visitObj.link}">${visitObj.link} <span class="glyphicon glyphicon-new-window"></span></a>`;

        const divInner3 = document.createElement("div");
        divInner3.className="col-md-3";
        divInner3.innerHTML=`<span style="font-size: larger;">${visitObj.date === null || visitObj.date.length === 0 ? "Review : N/A" : visitObj.date}</span>`;

        divOuter.appendChild(divInner1);
        divOuter.appendChild(divInner2);
        divOuter.appendChild(divInner3);
        hotelsParent.appendChild(divOuter)
    });
}
else{
    const superParent = document.getElementsByClassName('super_parent')[0];
    superParent.innerHTML = "<br><br><br><h2 style='text-align: center'>No Data Found. <br></h2><h3 style='text-align: center'>Please visits some expedia links via hotel page and come back</h3>";
}
clearButton.addEventListener('click',() =>{
   try {
       clearButton.innerHTML = 'Clearing...';
       const xhr = new XMLHttpRequest();
       xhr.withCredentials = true;

       xhr.addEventListener("readystatechange", function () {
           if (this.readyState === 4 && xhr.status === 204) {
               location.reload()
           }
       });
       xhr.open("delete", "/visited");
       xhr.send();
   }
   catch (e) {
       console.error(e)
   }
});