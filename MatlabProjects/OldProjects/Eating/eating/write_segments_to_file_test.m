sub_count = length(data)
min_th = -2.5;
var_th = 0.15;
stat_test = zeros(sub_count, 2);

segment_length = 6*16;
left = segment_length/2-1;
right = segment_length/2;

for sub = 1:sub_count
    fprintf('Subject: %d\n', sub);
    accel = data(sub).accel;
    
    accel_count = length(accel);
    seg_count = length(segment_length/2:segment_length/2:accel_count-segment_length/2);
    test_data = [];
    
    c = 0;
    for ind = segment_length/2:segment_length/2:accel_count-segment_length/2
        a = accel(ind-left:ind+right, 2:4);        
        m = min(a(:,1));        
        v = mean(var(a));         
        if m <= min_th && v >=var_th
            a = my_normalize(a);
            test_data = [test_data; a(:)', ind];
            c = c+ 1;
        end    
    end
    
    stat_test(sub, :) = [seg_count, c];
    csvwrite(strcat('segment_data/test/', num2str(sub)), test_data);
     
end

