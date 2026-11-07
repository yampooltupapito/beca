/**
 * SIBECA-18 | Validaciones en Cliente con jQuery y Bootstrap 5 (RF-04, Tema 10)
 * Nota: No utiliza Ajax ni librerías no permitidas.
 */
$(document).ready(function () {

    var regexDni = /^\d{8}$/;
    var regexCelular = /^9\d{8}$/;
    var regexCorreo = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,10}$/;

    // 1. Cálculo dinámico de edad y reglas orientativas en el formulario de Inscripción / Edición
    function actualizarAyudaModalidadYEdad() {
        var $selectMod = $("#idModalidad");
        if ($selectMod.length === 0) {
            return;
        }
        var $opcion = $selectMod.find("option:selected");
        var tipoMod = $opcion.data("tipo") || "";
        var edadMax = $opcion.data("edadmax");
        var puntajeMin = $opcion.data("puntajemin") || 66;
        var discapacidad = $("#discapacidadAcreditada").is(":checked");

        var textoRegla = "Modalidad " + ($opcion.text() || "") + " | Puntaje mínimo ENP: " + puntajeMin + " pts";
        if (discapacidad || !edadMax || edadMax === "null") {
            textoRegla += " | Límite de edad: Sin límite de edad (RN-02).";
        } else {
            textoRegla += " | Límite de edad: Menor de " + edadMax + " años a la fecha de corte (RN-02).";
        }
        if (tipoMod === "ORDINARIA") {
            textoRegla += " | Exige: Egreso 2023-2026 (RN-03), Tercio Superior (RN-04) y Pobreza/Pobreza Extrema (RN-05).";
        }
        $("#infoReglasModalidad").text(textoRegla);

        // Calcular edad aproximada a la fecha de corte (2026-10-09)
        var fechaNacStr = $("#fechaNacimiento").val();
        var fechaCorteStr = $("#fechaCorteHidden").val() || "2026-10-09";
        if (fechaNacStr) {
            var fn = new Date(fechaNacStr + "T00:00:00");
            var fc = new Date(fechaCorteStr + "T00:00:00");
            var edad = fc.getFullYear() - fn.getFullYear();
            var m = fc.getMonth() - fn.getMonth();
            if (m < 0 || (m === 0 && fc.getDate() < fn.getDate())) {
                edad--;
            }
            if (!isNaN(edad)) {
                $("#edadCalculadaPreview").text(edad + " años cumplidos al " + fechaCorteStr);
            }
        }
    }

    $("#idModalidad, #fechaNacimiento, #discapacidadAcreditada").on("change keyup", actualizarAyudaModalidadYEdad);
    actualizarAyudaModalidadYEdad();

    // 2. Validación en cliente del formulario de Inscripción / Edición (#formInscripcion)
    $("#formInscripcion").on("submit", function (e) {
        var errores = [];
        $(".is-invalid").removeClass("is-invalid");

        var dni = ($("#dni").val() || "").trim();
        var nombres = ($("#nombres").val() || "").trim();
        var apellidos = ($("#apellidos").val() || "").trim();
        var fechaNac = ($("#fechaNacimiento").val() || "").trim();
        var correo = ($("#correo").val() || "").trim();
        var celular = ($("#celular").val() || "").trim();
        var region = ($("#region").val() || "").trim();
        var colegio = ($("#colegio").val() || "").trim();
        var idModalidad = $("#idModalidad").val();

        if (!regexDni.test(dni)) {
            $("#dni").addClass("is-invalid");
            errores.push("El DNI debe contener exactamente 8 dígitos numéricos (RF-04).");
        }
        if (nombres.length < 2) {
            $("#nombres").addClass("is-invalid");
            errores.push("Ingrese los nombres completos del postulante.");
        }
        if (apellidos.length < 2) {
            $("#apellidos").addClass("is-invalid");
            errores.push("Ingrese los apellidos completos del postulante.");
        }
        if (!fechaNac) {
            $("#fechaNacimiento").addClass("is-invalid");
            errores.push("Seleccione su fecha de nacimiento.");
        }
        if (!regexCorreo.test(correo)) {
            $("#correo").addClass("is-invalid");
            errores.push("Ingrese un correo electrónico con formato válido (RF-04).");
        }
        if (!regexCelular.test(celular)) {
            $("#celular").addClass("is-invalid");
            errores.push("El celular debe tener 9 dígitos numéricos y empezar con el dígito 9 (RF-04).");
        }
        if (!region) {
            $("#region").addClass("is-invalid");
            errores.push("Seleccione la región de procedencia.");
        }
        if (colegio.length < 3) {
            $("#colegio").addClass("is-invalid");
            errores.push("Ingrese el nombre de la institución educativa de secundaria.");
        }
        if (!idModalidad || idModalidad === "0") {
            $("#idModalidad").addClass("is-invalid");
            errores.push("Seleccione una modalidad de postulación.");
        }

        var $pass = $("#password");
        if ($pass.length > 0 && ($pass.val() || "").trim().length < 6) {
            $pass.addClass("is-invalid");
            errores.push("La contraseña debe tener como mínimo 6 caracteres.");
        }

        // Si el usuario activó el modo de prueba de validación en servidor, permitir envío al Servlet
        var permitirEnvioServidor = $("#omitirValidacionCliente").is(":checked");
        if (errores.length > 0 && !permitirEnvioServidor) {
            e.preventDefault();
            var htmlLista = "<ul class='mb-0 ps-3'>";
            for (var i = 0; i < errores.length; i++) {
                htmlLista += "<li>" + errores[i] + "</li>";
            }
            htmlLista += "</ul>";
            $("#alertaErroresClienteTexto").html(htmlLista);
            $("#alertaErroresCliente").removeClass("d-none").hide().fadeIn(200);
            window.scrollTo({ top: 0, behavior: "smooth" });
        }
    });

    // 3. Validación en cliente del formulario de Login (#formLogin)
    $("#formLogin").on("submit", function (e) {
        var user = ($("#username").val() || "").trim();
        var pass = ($("#password").val() || "").trim();
        if (!user || !pass) {
            e.preventDefault();
            $("#alertaLoginCliente").removeClass("d-none").text("Complete su usuario y contraseña para iniciar sesión.");
        }
    });

    // 4. Validación en cliente de Revisión Documental (Observación obligatoria si es NO APTO - RF-10, CP-11)
    $("#formDictaminarRevision").on("submit", function (e) {
        var resultado = $("input[name='resultadoRevision']:checked").val();
        var obs = ($("#observacionRevision").val() || "").trim();
        var omitirCliente = $("#omitirValidacionRevisionCliente").is(":checked");
        if (resultado === "NO APTO" && obs.length === 0 && !omitirCliente) {
            e.preventDefault();
            $("#observacionRevision").addClass("is-invalid");
            $("#errorRevisionCliente").removeClass("d-none")
                .text("RF-10 / CP-11: Cuando el resultado es NO APTO, es obligatorio escribir la observación de sustento.");
        }
    });

    // 5. Validación en cliente de Puntaje del ENP (0 a 120 - RN-09)
    $("#formPuntajeEnp").on("submit", function (e) {
        var valor = parseFloat(($("#puntajeEnp").val() || "").replace(",", "."));
        var omitirCliente = $("#omitirValidacionEnpCliente").is(":checked");
        if ((isNaN(valor) || valor < 0 || valor > 120) && !omitirCliente) {
            e.preventDefault();
            $("#puntajeEnp").addClass("is-invalid");
            $("#errorEnpCliente").removeClass("d-none")
                .text("RN-09: El puntaje del ENP debe ser un número entre 0 y 120.");
        }
    });

    // 6. Rellenar datos automáticos al elegir una institución en el formulario de programas
    $("#selectInstitucionPrograma").on("change", function () {
        var $opt = $(this).find("option:selected");
        if ($opt.val()) {
            $("#institucionTexto").val($opt.data("nombre"));
            $("#tipoGestionPrograma").val($opt.data("gestion"));
            $("#regionPrograma").val($opt.data("region"));
        }
    });
});
