//auto-hide dopo 4 secondi
//seleziono tutti gli elemnti con la classe alter e per ciascuno imposto il timer
//cioè nascondo l'elemento dopo tot tempo
document.addEventListener("DOMContentLoaded", () =>{

    document.querySelectorAll(".alert").forEach(a => {
        setTimeout(()=> {a.style.display='none'},4000);//a.classList.add()
    });
});


document.querySelectorAll("form[data-confirm]").forEach(
    form => { 
        form.addEventListener("submit",(e) => {
            const msg = form.getAttribute("data-confirm") ||
                                            "Sei sicuro?";
            if(!window.confirm(msg))
                e.preventDefault();
        })

    }
)