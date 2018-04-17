tp = zeros(10,1);
fp_hw = zeros(10,1);
fp_eat = zeros(10,1);
for s = 1:10
    ai = data(s).annot_indices;
    ai_count = length(ai);
   
    fprintf('%d\n', s);
    res = harmony_results(s).res_hw;
    %selected = harmony_data(s).selected_windows(s).hw;
    %res = res.*selected;
    res_hw = res(1:2:end, :);

    res = harmony_results(s).res_eat;
    %selected = gen_data(s).ico(ic).selected_windows(s).eat;
    %res = res.*selected;
    res_eat = res(1:2:end, :);

    events = find_events(res_eat);
    fp_eat(s) = length(events);
    
    
    events = find_events(res_hw);    
    if isempty(events)
        tp(s) = tp(s) + 1;
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
            tp(s) = tp(s) + 1;
        end            
    end
    
    event_count =size(events, 1);
    for j = 1:event_count
        a = events(j,1);
        b = events(j,2);
        c1 = sum(ai(:,1)<=a & ai(:,2)>=b);
        c2 = sum(ai(:,1)<=b & ai(:,2)>=a);            
        c3 = sum(ai(:,1)>=a & ai(:,2)<=b);
        if c1+c2+c3 >0
            fp_hw(s) = fp_hw(s) + 1;
        end            
    end
    
    
end

x = sum(tp)
y = sum(fp_hw)
z = sum(fp_eat)