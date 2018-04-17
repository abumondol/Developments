function res = ward_append_adjust_time(prev_data, new_data)
    if isempty(prev_data)
        res = new_data;
        return;
    end
    
    tend = prev_data(end, 1);
    sample_period = prev_data(end,1) - prev_data(end-1,1);
    
    new_data(:, 1) = new_data(:,1) + tend + sample_period;    
    res = [prev_data; new_data];    
end