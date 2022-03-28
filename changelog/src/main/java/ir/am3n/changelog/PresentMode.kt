package ir.am3n.changelog

enum class PresentMode {

    // Ignore the change of version name and version code, present always.
    DEBUG,

    // Check both of version code number and version name.
    // Try to split the version name to numbers firstly.
    // Check if one of the first two elements is higher than previous one.
    // If positive, present.
    // Or then check the version code like option [ALWAYS].
    IF_NEEDED,

    // Just check the version code. Present if the version code number is
    // higher than the previous one.
    ALWAYS,

    // Never present.
    NEVER

}