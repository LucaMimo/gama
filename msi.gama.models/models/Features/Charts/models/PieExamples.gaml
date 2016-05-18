/**
 *  newtest
 *  Author: HPhi
 *  Description: 
 */

model newtest

global
{
}

experiment my_experiment type: gui {
	float minimum_cycle_duration<-0.2;
	
	output {
		display "data_pie_chart" type:java2D {
			chart "Nice Ring Pie Chart" type:pie style:ring
			 	background: #darkblue
			 	color: #lightgreen 
			 	axes: #yellow
			 	title_font: 'Serif'
			 	title_font_size: 32.0
			 	title_font_style: 'italic'
			 	tick_font: 'Monospaced'
			 	tick_font_size: 14
			 	tick_font_style: 'bold' 
			 	label_font: 'Arial'
			 	label_font_size: 32
			 	label_font_style: 'bold' 
			 	x_label:'Nice Xlabel'
			 	y_label:'Nice Ylabel'
			 	
			{
				data "BCC" value:100+cos(100*cycle)*cycle*cycle
				color:#black;
				data "ABC" value:cycle*cycle 
					color:#blue;
				data "BCD" value:cycle+1;
			}
		} 
 		
		display "data_3Dpie_chart" type:java2D {
			chart "data_3Dpie_chart" type:pie style:"3d" {
				data "BCC" value:2*cycle
				color:#black;
				data "ABC" value:cycle*cycle 
					color:#blue;
				data "BCD" value:cycle+1;
			}
		} 
 		
		display "datalist_pie_chart" type:java2D {
			chart "datalist_pie_chart" type:pie style:exploded{
				datalist legend:["A","B","C"] 
					value:[[cycle,cycle+1,2],[cycle/2,cycle*2,1],[cycle+2,cycle-2,cycle]] 
					x_err_values:[3,2,10]
					y_err_values:[3,cycle,2*cycle]
//					categoriesnames:["C1","C2","C3"]
					color:[#black,#blue,#red];
			}
		}
	}
}