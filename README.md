# MuleLint

MuleLint is a powerful static code analysis and property validation plugin for MuleSoft Anypoint Studio (Eclipse).  
It helps developers detect missing or unused properties early in the development lifecycle, ensuring robust and clean configuration files.

---

## 🚀 Features

### ✅ Property Validation
- **Deep Scanning**: Analyzes `*.xml` and `*.dwl` files for property references (`${}`, `p('')`, `Mule::p('')`).
- **YAML Intelligence**: Flattens nested YAML configuration files to validate against all possible keys.
- **Missing Detection**: Flags any property used in code but missing from configuration files.
- **Unused Detection**: Identifies dead properties in YAML files that are no longer referenced in your project.
- **Smart Filtering**: Categorize results into **Valid**, **Missing**, and **Unused** using dedicated UI filters.
- **Real-time Search**: Search through thousands of properties instantly within the custom view.

### 🛠️ Developer Experience
- **Dedicated View**: A custom "Mule Property Validation" tab for clear reporting.
- **Problems Integration**: Native Eclipse markers show errors directly on the lines of code.
- **Auto-Save**: Automatically saves modified editors before validation to ensure accuracy.
- **Keyboard Shortcut**: Trigger validation instantly with `Ctrl+Shift+V` (Windows/Linux) or `Cmd+Shift+V` (macOS).

---

## 🛠️ Packaging the Plugin

To build the plugin from source, you need **Java 11+** and **Maven** installed.

1. Clone the repository.
2. Navigate to the project root directory.
3. Run the following command:
   ```bash
   mvn clean install
   ```
4. The compiled plugin JAR will be located at:
   `target/com.devsha256.mulelint-1.0.0-SNAPSHOT.jar`

---

## 💻 Installation Instructions

The easiest way to install the plugin is via the `dropins` folder.

### Windows 11
1.  Close **Anypoint Studio**.
2.  Navigate to the folder where you unzipped/installed Anypoint Studio (e.g., `C:\AnypointStudio-7.x\`).
3.  You will see a folder named **`dropins`** in the same directory as `AnypointStudio.exe`.
4.  Copy the `com.devsha256.mulelint-1.0.0-SNAPSHOT.jar` file into this **`dropins`** folder.
5.  Restart **Anypoint Studio**.

### macOS
1.  Close **Anypoint Studio**.
2.  Open your **Applications** folder in Finder.
3.  Right-click on the **AnypointStudio.app** icon and select **Show Package Contents**.
4.  Navigate to **`Contents` > `Eclipse` > `dropins`**.
5.  Copy the `com.devsha256.mulelint-1.0.0-SNAPSHOT.jar` file into this **`dropins`** folder.
6.  Restart **Anypoint Studio**.

> [!TIP]
> If the plugin does not appear after restarting, try starting Anypoint Studio once with the `-clean` flag or delete the `configuration/org.eclipse.update` folder to force a refresh of the plugin cache.


---

## 🎯 Usage

1. **Run Validation**: Right-click on any Mule project in the Package Explorer and select **Run Mule Property Validation**, or use the keyboard shortcut `Cmd/Ctrl + Shift + V`.
2. **View Results**: Open the **Mule Property Validation** view via `Window > Show View > Other... > MuleLint > Mule Property Validation`.
3. **Filter & Search**: Use the radio buttons at the top of the view to filter by "Errors" or "Warnings," and use the search box to find specific keys.
4. **Refresh**: Use the **Refresh** icon in the view toolbar to re-run validation on the last project.

---

## ⚙️ Configuration

Customize which files are scanned by going to:
`Window > Preferences > MuleLint Configuration`

- **Source File Extensions**: (Default: `xml,dwl`)
- **Target File Extensions**: (Default: `yaml,yml`)

---

## 🧩 License
Apache License 2.0
