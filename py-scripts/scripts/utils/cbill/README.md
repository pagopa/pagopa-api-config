# Import CBILL codes

This python script is used to import the `cbill` codes inside the field `CBILL`
 of the table `NODO4_CFG.PA` starting from the `CSV` file coming from CBILL.

Usually the file name is structured in following the pattern below:

`"ListaXPagoPA" + "_" + <gg><month>.csv`

To launch the import is necessary to place the `csv` file in the same folder of the scripts and launch the following 
command:

`python3 import-cbill.py <filename>.csv`