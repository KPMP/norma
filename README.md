# norma
Repository to hold the json documents and generator for dynamic forms in orion


In order to run the DTD generator, you’ll need to create a credentials file and save it in the src/main/resources directory.
To do that, click on “Enable Google Sheets API” here: https://developers.google.com/sheets/api/quickstart/java. When you run this for the first time it will have you login and then write a permanent credentials file.

To generate the DTD:

`gradle -q run`
or
`gradle runGenerateDTD`

To run the Metadata Sheet validator:

`gradle runValidateMetatdataSheet`

