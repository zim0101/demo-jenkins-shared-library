def call() {
    sh(script: "python3 --version")
    sh(script: "pip install .")
    sh(script: "pytest")
}
