function ward_res = get_ward_res(oppdata, res, window_size, step_size) 

    window_size = 30;
    step_size = 15;

    t_all=[];
    ground_all=[];
    eval_all = [];

    for subj=1:4
        for sess=1:5        
            t = oppdata{subj, sess}.t;
            if isempty(t_all)
                t_all = t;
            else
                t = t +  t_all(end) + t(2)-t(1);
                t_all = [t_all; t];
            end

            YPred = res{subj,sess}.YPred;
            
            eval = benchmark_expandingLabels(YPred, window_size, length(t), step_size);
            eval_all = [eval_all; eval];

            ground = oppdata{subj, sess}.labels(:,5);
            ground = rename_labels(ground);        
            ground_all= [ground_all;ground];        
        end
    end

    class_count = length(unique(eval_all));

    t = t_all;
    t2 = [t(2:end, :); t(end)+t(2)-t(1)];
    ground = [t, t2, ground_all];
    eval = [t, t2, eval_all];        

    [ward_t, ward_s, ward_e, ward_meanLenOU] = benchmark_mset(eval, ground, class_count);

    ward_res = benchmark_ward_errors({ward_t});
    ward_res = ward_res{1};

end