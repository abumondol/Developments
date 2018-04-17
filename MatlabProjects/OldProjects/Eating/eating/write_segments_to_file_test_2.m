sub_count = length(data)
stat = zeros(sub_count, 3);

for sub=1:sub_count
    fprintf('Subject: %d\n', sub);
    accel = data(sub).accel;    
    seg = segments(sub).segments;    
    count = length(seg);
    
    data_left = [];
    data_right = [];
    for i = 1:count
        if seg(i, 2)<=-2.5 && seg(i,3)>=0.2             
            ind = seg(i,1);                        
            
            dt = accel(ind-39:2:ind, 2:4);
            %dt = my_normalize(dt);                
            data_left = [data_left; dt(:)', ind];

            dt = accel(ind:2:ind+39, 2:4);
            %dt = my_normalize(dt);
            dt = flipud(dt);
            data_right = [data_right; dt(:)', ind];            
        end
    end
    stat(sub, :)  = [count, length(data_left), length(data_right)];
    
    csvwrite(strcat('segment_data/test_left/left_', num2str(sub)), data_left);
    csvwrite(strcat('segment_data/test_right/right_', num2str(sub)), data_right);    
end
