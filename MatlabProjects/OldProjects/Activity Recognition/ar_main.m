close all;
clc;

read_raw_data();
process_data();
quaternion_to_grav();
createDictionary(0.1, 1.0);

draw_graph();
draw_graph_dictionary();