function createDictionary(query_length, distance_threshold)
load('data');
len = length(data);
fprintf('Creating Dictioanry. Total data: %d\n', len);

min_distances = [];
dict = [];
k = 1;
sequence = [];
for i=1:len
    
%    data(i).quantized_xy = quantize_data(data(i).grav_quat(:, 1:2));
%     data(i).quantized_yz = quantize_data(data(i).grav_quat(:, 2:3));
%     data(i).quantized_zx = quantize_data(data(i).grav_quat(:, [3 1]));
    d =  data(i).grav_quat(:, 1:2);
    indices = find_query_indices(d, query_length); 
    data(i).query_indices = indices;   
    total_indices = length(indices);
    
    start = 3;
    if length(dict) == 0
        window = d(indices(1,1):indices(3,1), :); 
        dict(k).data = window   
        dict(k).count = 0;
        dict(k).series_no = i;
        k = k + 1;
        last_window = d(indices(2,1):indices(3,1), :); 
        last_window_new = 1; % 1: the window found for first time, 0: otherwise
    else
        window = d(indices(1,1):indices(2,1), :);
        [min_dis, nn_index] = nearest_neighbour(window, dict);
        if min_dis <= distance_threshold % window contained by dict(nn_index)
            dict(nn_index).count = dict(nn_index).count + 1;
            dict(nn_index).windows(dict(nn_index).count).data = window;
            last_window_new = 0;
        else
            last_window_new = 1;
        end
        last_window = window;
        start = 2;        
    end
    
    
    for j = start:total_indices-1
        window = d(indices(j,1):indices(j+1,1), :);
        [min_dis, nn_index] = nearest_neighbour(window, dict);
        min_distances = [min_distances; min_dis];
        if min_dis <= distance_threshold % window contained by dict(nn_index)
            dict(nn_index).count = dict(nn_index).count + 1;
            dict(nn_index).windows(dict(nn_index).count).data = window;
            if last_window_new == 1
                dict(k).data = [last_window;window];  
                dict(k).count = 0;
                dict(k).series_no = i;
                k = k + 1;
            end
            last_window = window;
            last_window_new = 0;
        else
            dict(k).data = [last_window;window];  
            dict(k).count = 0;
            dict(k).series_no = i;
            k = k + 1;
            last_window = window;
            last_window_new = 1;
        end
        fprintf('k = %d\n',k);        
    end
    
end
sort(min_distances)
size(dict)
save('data','data');
save('dict','dict');
fprintf('Creating Dictioanry Done \n');
end