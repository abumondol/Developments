ic = 2;
nb_num = 0;

all_counts = zeros(10,4);

for s = 1:10
    train_freq = get_train_cell_freq(ico, ic, nb_num, gen_data, s);
    
    w_hw = gen_data(s).windows_hw(:, [ic, 6]);
    w_eat = gen_data(s).windows_eat(:, ic);
    
    pos = w_hw(w_hw(:,2)==1, 1);
    neg = w_hw(w_hw(:,2)==0, 1);
    %neg = [neg; w_eat];

    pos_count = length(pos);
    neg_count = length(neg);

    pos_new = 0;
    for i=1:length(pos)
        if train_freq(pos(i))>0
            pos_new = pos_new + 1;
        end
    end

    neg_new = 0;
    for i=1:length(neg)
        if train_freq(neg(i))>0
            neg_new = neg_new + 1;
        end
    end

    all_counts(s, :) = [pos_count, neg_count, pos_new, neg_new];    
end

res = sum(all_counts)