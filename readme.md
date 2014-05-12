Topsoil
=======

Topsoil is a standalone application and graphing library that provides essential
functionality for geochronologists and other earth scientists. This project is
led and maintained by [CIRDLES](http://cirdles.org), an undergraduate research
lab at the College of Charleston in Charleston, South Carolina.

"Topsoil" is an anagram of "[Isoplot](http://bgc.org/isoplot_etc/isoplot.html)",
which is the name of a now defunct Microsoft Excel plugin that Topsoil is
designed to replace.

If you are interested in learning more about Topsoil or making suggestions for
the project, please contact [Jim Bowring](mailto://bowringj@cofc.edu) or create
an enhancement issue in this project's issue tracker.

Standalone Usage
----------------

### Installation

We'll update this section after the first official release.

### Getting Started

When initially running Topsoil, you'll see that the data table is empty, so we
must first import data from an external source. Topsoil currently supports the
following data import methods:

##### Copy-paste from Microsoft Excel

1. Open your .XLS or .XLSX spreadsheet with Excel.
2. Select and copy (Ctrl-c for Windows or ⌘-c for Mac) the rows and columns
   that you want to import in Excel. Note that Topsoil can only handle one row
   of headers.
3. Return to the Topsoil window and click on the empty table. Paste (Ctrl-p for
   Windows or ⌘-p for Mac) the Excel data here.
4. You should now be asked whether or not your data contains headers. Click
   "Yes" or "No" to indicate your choice, import the data, and continue. 
   Otherwise, you can click "Cancel" to cancel pasting and import data again
   using this or another method.

##### Import from .TSV

1. We'll update this section after we actually support .TSV file import.

#### Moving On

You should now see your data in the table. If your data is incomplete or
incorrect after import, please file an issue detailing the problem in this
project's issue tracker.

Topsoil will remember the last dataset that it contained, so the next time you
run it, your data should be waiting for you.

If you want to import another dataset and there is already data in the table,
don't worry about it! Simply follow one of the import methods above, and Topsoil
will overwrite the old data.

#### Creating Charts

Now that we have data in our table, we're able to begin using Topsoil's graphing
capabilities. Before continuing this tutorial, please make sure that your table
contains _at least_ 5 columns. For best results, please use data that is meant
to be plotted as an error ellipse.

In this example, we will create an error ellipse chart from our table. To start,
first click the "Error Ellipse Chart" button in the tool bar. You should now be
prompted by a dialog to select column bindings.

In this step, we'll choose the columns that will represent each of the variables
needed to generate the error ellipse chart. By default, x, σx, y, σy, and ρ are
bound to the first five columns in that order. To bind a variable to a different
column, use the corresponding drop down menu to select the column by name. When
you are satisfied with your settings, press "Create chart" to generate your new
error ellipse chart.

#### Wrapping Up

Voilà! The chart should now appear in a new window on your desktop. Try moving
around by clicking and dragging. Additionally, you can perform a "box zoom" by
right-clicking and dragging a box inside the chart space or a "point zoom" by
rolling your mouse wheel either in or out. Feel free to play around with any of
the settings at the top of the chart window. Think that two charts are better
than one? Return to the main window and repeat the last section to create a
second one with different parameters.