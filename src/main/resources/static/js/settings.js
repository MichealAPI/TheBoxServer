document.getElementById("saveSettingsBtn").addEventListener("click", function(e) {

    e.currentTarget.disabled = true;
    e.currentTarget.innerText = "Saving...";

    const settings = document.querySelectorAll(".setting");

    let settingsObj = {};

    settings.forEach(setting => {

        // Setting name ID
        let settingName = setting.getAttribute("data-setting-id");

        // Setting value
        let settingDataType = setting.getAttribute("data-setting-type");

        let inputElement = setting.querySelector(".content input");

        settingsObj[settingName] = settingDataType === "boolean" ? inputElement.checked.toString() : inputElement.value.toString();

    });

    fetch("/api/settings/update", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(settingsObj)
    }).then((res) => {
        console.log(res);
        location.reload();
    }).catch(err => {
        console.error("Error saving settings: ", err);
    })

});

function retrieveSettings() {

    fetch("/api/settings/retrieve")
        .then(response => response.json())
        .then(data => {

            const settings = document.querySelectorAll(".setting");

            settings.forEach(setting => {

                let settingName = setting.getAttribute("data-setting-id");

                let inputElement = setting.querySelector(".content input");

                let settingDataType = setting.getAttribute("data-setting-type");

                if (settingDataType === "boolean") {
                    inputElement.checked = data[settingName] === "true";
                } else {
                    inputElement.value = data[settingName];
                }

            });

        }).catch(err => {
            console.error("Error retrieving settings: ", err);
        });

}

// Todo: loader
retrieveSettings();