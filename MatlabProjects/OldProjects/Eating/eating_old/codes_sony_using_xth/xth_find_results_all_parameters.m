load('procdata');
load('procdata_noneat');
sub_count = length(procdata);
all_param_res = [];

for sub = 1:sub_count
    fprintf('Subject %d\n', sub);
    data = procdata;
    data_noneat = procdata_noneat;
    data(sub) = [];
    data_noneat(sub) = [];    
    all_param_res(sub).results = xth_find_results_all_parameters_one_subject(data, data_noneat);
end

save('all_param_res', 'all_param_res');
    