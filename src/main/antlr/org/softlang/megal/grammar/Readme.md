# Grammar hints

## Statement:
* Optional: Label of entire statement statement
* Three nodes
* * Source symbol
* * Connector symbol
* * Target symbol
* Any number: Cartesian product of target symbol
* Optional: primary symbol binding
* Indented: Repeated connector and target

### In Megal notation, this covers:
* ETD `type < supertype`
* RTD `type < domain * range`
* ED `name : type`
* RD `source relationship target`
* LD `name = "value"`

### In extended Megal notation, this covers:
* Labeled statements `label' type < supertype`
* Extended binding `xmlPlugin = {'class':'plugins.Plugin', 'instantiation': 
'singleton' }`
* Function definition `f: Lang * Lang`
* Parameterized types `items: Collection(Artifact)`
* Continuation `a : File = "binding.xml" \n\t elementOf XML \n\t uses 
Namespaces`