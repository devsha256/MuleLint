# MuleLint

MuleLint is a static code analysis and linting tool for MuleSoft applications.  
It helps developers detect configuration issues, enforce best practices, and maintain high-quality Mule codebases.

---

## 🚀 Features

### ✅ Property Validation (Phase 1)
- Scans Mule source files:
  - `*.xml` (Mule configs)
  - `*.dwl` (DataWeave scripts)
- Detects property references like:
  - `Mule::p("customer.zip_default")`
  - `p('customer.zip_default')`
- Validates against:
  - `*.yaml`
  - `*.yml`
- Supports:
  - Nested YAML structures
  - Flattened property keys
- Reports:
  - Missing properties
  - Valid references

---

## 🎯 Why MuleLint?

Mule applications often rely heavily on externalized configurations.  
Broken or missing properties can lead to runtime failures that are hard to debug.

MuleLint helps you catch these issues **early during development**.

---

## 🧩 Planned Features

- 🔍 Additional linting rules (naming conventions, flow structure, etc.)
- ⚡ Quick fixes (auto-create missing properties)
- 🧠 Intelligent suggestions (fuzzy matching for typos)
- 🌍 Environment-aware validation (dev/test/prod configs)
- 📊 Code quality scoring
- 🔌 Pluggable rule engine

---

## 🛠️ Usage

### Eclipse Plugin (Planned)
- Right-click on a Mule project
- Run: `MuleLint → Validate Properties`

### CLI (Future)
```bash
mulelint scan ./my-mule-project
