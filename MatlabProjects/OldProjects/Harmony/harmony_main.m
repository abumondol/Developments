close all;

window_size = 500;
step_size = 200;
getData(window_size, step_size);

features=1:15;
classify(features);

return
% The following code is for getting some raw_data and features to test them
% in Java to see that the java code is consistent
load('harmony_raw_data_processed'); % data_hw and data_nhw
load('harmony_window_indices'); % indices_hw and indices_nhw
load('harmony_features'); % feature_instances_pos and feature_instances_neg

ind = indices_hw{1,1}; 
size(ind)
si = ind(12,1)
ei = ind(12,2)
d = data_hw{1,1};
d=d(si:ei,:);
d = d(d(:,2)==1, 3:5);
dlmwrite('data.csv',d);
%dlmwrite('features.csv',feature_instances_pos);



% axes = [4, 5];
% neg_weight= gridAnalysis(axes);
% selected_instances_neg = feature_instances_neg(neg_weight(:,1)>0.1,:);
% size(selected_instances_neg)



return

