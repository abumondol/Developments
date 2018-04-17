[window_length, annot_type, free_length, step_size, x_th, var_th] = get_settings()
sub_count = length(data);

for sub = 1:sub_count
    fprintf('Subject %d\n', sub);    
    testX = get_test_data(data(sub).accel(:, 2:4), window_length, step_size, x_th, var_th);
    size(testX)
    csvwrite(strcat('test_data/', num2str(sub)), testX);    
end

