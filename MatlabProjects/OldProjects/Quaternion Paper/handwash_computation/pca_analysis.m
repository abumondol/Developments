res = [];
for factor= -0.1:0.01:0.15
    fprintf('factor: %d\n', factor);
    all_counts = zeros(10, 6);
    for s = 1:10
        xyz_train = pca_get_train(gen_data, s);

        xyz_hw = gen_data(s).xyz_hw;
        xyz_eat = gen_data(s).xyz_eat(:, 1:3);

        pos = xyz_hw(xyz_hw(:,end)==1, 1:3);
        neg = xyz_hw(xyz_hw(:,end)==0, 1:3);
        neg2 = [neg; xyz_eat];

        min_lim = min(xyz_train) - factor;
        max_lim = max(xyz_train) + factor;

        pos_count = length(pos);
        neg_count = length(neg);
        neg_count2 = length(neg2);

        pos_new = 0;
        for i=1:length(pos)
            if pos(i,1)>=min_lim(1) && pos(i,2)>=min_lim(2) && pos(i,3)>=min_lim(3) && pos(i,1)<=max_lim(1) && pos(i,2)<=max_lim(2) && pos(i,3)<=max_lim(3)
                pos_new = pos_new + 1;
            end
        end

        neg_new = 0;
        for i=1:length(neg)
            if neg(i,1)>=min_lim(1) && neg(i,2)>=min_lim(2) && neg(i,3)>=min_lim(3) && neg(i,1)<=max_lim(1) && neg(i,2)<=max_lim(2) && neg(i,3)<=max_lim(3)
                neg_new = neg_new + 1;
            end
        end

        neg_new2 = 0;
        for i=1:length(neg2)
            if neg2(i,1)>=min_lim(1) && neg2(i,2)>=min_lim(2) && neg2(i,3)>=min_lim(3) && neg2(i,1)<=max_lim(1) && neg2(i,2)<=max_lim(2) && neg2(i,3)<=max_lim(3)
                neg_new2 = neg_new2 + 1;
            end
        end
        
        all_counts(s, :) = [pos_count, pos_new, neg_count, neg_new, neg_count2, neg_new2];    
    end

    res = [res; factor, sum(all_counts)];
end

pos_count = res(:, 2);
pos_new = res(:, 3);
neg_count = res(:, 4);
neg_new = res(:, 5);
neg_count2 = res(:, 6);
neg_new2 = res(:, 7);

frr = 100*(pos_count-pos_new)./pos_count;
pr1 = 100*(pos_count+neg_count-pos_new-neg_new)./(pos_count+neg_count);
pr2 = 100*(pos_count+neg_count2-pos_new-neg_new2)./(pos_count+neg_count2);

format long g
res = [res, frr, pr1, pr2];
for i =1:size(res, 1)
    fprintf('Factor %.2f: ', res(i,1));
    for j=2:7
        fprintf('%d, ', res(i,j));
    end
    for j=8:10
        fprintf('%.2f, ', res(i,j));
    end
    fprintf('\n');    
end
