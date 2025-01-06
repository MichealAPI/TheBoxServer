document.getElementById("saveSettingsBtn").addEventListener("click", function(e) {

    console.log("Save settings button clicked");

    const settings = document.querySelectorAll(".setting");

    let settingsObj = {};

    settings.forEach(setting => {

        // Setting name ID
        let settingName = setting.getAttribute("data-setting-id");

        // Setting value
        let settingDataType = setting.getAttribute("data-setting-type");

        let inputElement = setting.querySelector(".content input");

        let value = settingDataType === "boolean" ? inputElement.checked.toString() : inputElement.value.toString();

        settingsObj[settingName] = value;

    });

    fetch("/api/settings", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(settingsObj)
    })

});