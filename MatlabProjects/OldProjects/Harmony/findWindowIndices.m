function findWindowIndices(window_size, step_size)
load('harmony_raw_data_processed');
indices_hw = {};
indices_nhw = [];
for i=1:size(data_hw,1)
    indices_hw{i,1} = findWindowIndicesSingle(data_hw{i,1}, window_size, step_size);    
end
indices_nhw = findWindowIndicesSingle(data_nhw, window_size, step_size);  
save('harmony_window_indices', 'indices_hw', 'indices_nhw');

end


function windowIndices = findWindowIndicesSingle(data, window_size, step_size)
% windowSize and stepSize are time here
windowIndices =[];
len = size(data, 1);
i=1;
while i < len    
    t2 = data(i,1) + window_size; 
    start_index = i;
    j = i;
    while data(j,1) < t2
        if j >= len
            return
        end
        j = j + 1;        
    end
    end_index = j-1;
    
    windowIndices = [windowIndices; start_index, end_index];
    
    t2 = data(i,1) + step_size;     
    while data(i,1) < t2
        if i >= len 
            return
        end
        i = i + 1;        
    end    
end

end

