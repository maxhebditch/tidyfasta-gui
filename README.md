# Tidy FASTA GUI
![Build](https://github.com/maxhebditch/tidyfasta-gui/workflows/Build/badge.svg)

An open source and cross platform application to fix, and find problems in protein FASTA sequence files.

![Reformat whitespace](images/tidyFASTA-reformat.png)

![Non-canonical AA](images/tidyFASTA-badAA.png)

### Features
tidyFASTA is a cross platform application (Windows, Mac OS X, Linux) and is available free of charge and without registration.

## Problems and fixes

| Problem                     | Fix (Strict mode)                       |
|-----------------------------|-----------------------------------------|
| Sequence without ID         | ID name added                           |
| Multiline sequence          | One line per sequence                   |
| ID without sequence         | Sequence ignored (Exception raised)     |
| Non canonical AA            | Sequence ignored (Exception raised)     |
| Lowercase AA                | Converts to uppercase AA                |
| Excessive Whitespace        | Removes excessive whitespace            | 

### Installation
1. Install java (if required)
    + If Java is not already installed on your computer (version 11 or greater), it is available [here](https://www.oracle.com/java/technologies/javase-jdk14-downloads.html) for all major operating systems.
2. Download the jar file from the [release tab](https://github.com/maxhebditch/tidyfasta-gui/releases).
3. Run the tidyFASTA-gui file by opening the file, or from the command line.

### Security
tidyFASTA is open source and all processing is run entirely on your local machine.

### Alternatives
tidyFASTA is also available as a python [package](https://github.com/maxhebditch/tidyfasta).
