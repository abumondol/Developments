function calculateFeatures()

display('Calculating features ...');
load('harmony_raw_data_processed'); % data_hw and data_nhw
load('harmony_window_indices'); % indices_hw and indices_nhw

features_per_sensor = 15;
sensors = [1 4 9 10];
total_features = length(sensors)*features_per_sensor;

len = size(indices_hw,1);
s = 0;
for i=1:len
    s = s + size(indices_hw{i,1},1);
end

feature_instances_pos = zeros(s,total_features+1);

j=1;
for i=1:len
    ind = indices_hw{i,1};     
    data = data_hw{i,1};    
    l = size(ind, 1);            
    for k = 1:l
        f=[];
        si = ind(k,1);
        ei = ind(k,2);
        d2 = data(si:ei, :);
        for sen=sensors
            d = d2(d2(:,2)==sen, 3:5);
            f = [f, calcluateWindowFeatures(d)];
        end
        f = [f, i];
        feature_instances_pos(j,:) = f;
        j = j + 1;
    end
end

ind = indices_nhw;     
data = data_nhw;
l = size(ind, 1)            
feature_instances_neg = zeros(l,total_features);
for k = 1:l
    f=[];
    si = ind(k,1);
    ei = ind(k,2);
    d2 = data(si:ei, :);
    for sen=sensors
        d = d2(d2(:,2)==sen, 3:5);
        f = [f, calcluateWindowFeatures(d)];
    end    
    feature_instances_neg(k,:) = f;    
end

save('harmony_features','feature_instances_pos','feature_instances_neg');

display('Calculating features done');
end