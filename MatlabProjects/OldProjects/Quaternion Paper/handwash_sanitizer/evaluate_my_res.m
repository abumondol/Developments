tp = zeros(10,5);
fp_hw = zeros(10,5);
fp_eat = zeros(10,5);
for s = 1:10
    ai = data(s).annot_indices;
    ai_count = length(ai);
    for ic = 1:5
        fprintf('%d, %d\n', s, ic);
        res = my_results(s).ico(ic).res_hw;
        selected = gen_data(s).ico(ic).selected_windows(s).hw;
        res = res.*selected;
        res_hw = res(1:2:end, :);
        
        res = my_results(s).ico(ic).res_eat;
        selected = gen_data(s).ico(ic).selected_windows(s).eat;
        res = res.*selected;
        res_eat = res(1:2:end, :);
        
        events = find_events(res_hw);
        if isempty(events)
            tp(s,ic) = tp(s,ic) + 1;
            continue
        end

        events = events((events(:,2) - events(:,1))>=3, :);
        events(:,1) = (events(:,1)-1)*50+1;
        events(:,2) = events(:,1)*50;
        
        for j = 1:ai_count
            a = ai(j,1);
            b = ai(j,2);
            c1 = sum(events(:,1)<=a & events(:,2)>=b);
            c2 = sum(events(:,1)<=b & events(:,2)>=a);            
            c3 = sum(events(:,1)>=a & events(:,2)<=b);
            if c1+c2+c3 >0
                tp(s,ic) = tp(s,ic) + 1;
            end            
        end
    
    end
end

x = sum(tp)