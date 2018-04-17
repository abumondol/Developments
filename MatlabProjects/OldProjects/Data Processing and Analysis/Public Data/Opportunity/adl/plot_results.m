load('allres');
alpha_vals = 1:0.5:4;

for k=3:3
    db = [];
    d1 = [];
    d2 = [];
    for aix = 1:length(alpha_vals)
        db = [db; allres{k, aix}.prf_base];
        d1 = [d1; allres{k, aix}.prf_ours1];
        d2 = [d2; allres{k, aix}.prf_ours2];
    end
    
    figure
    plot([0.5;4.5], db(1:2,1), 'LineWidth',3);
    ylim([0,1]);
    hold on
    bar(alpha_vals, [d1(:,1),d2(:,1)]);
    xlabel('\alpha', 'FontSize', 20);
    title(strcat('Precision (k=',num2str(k),')'), 'FontSize', 16);
    lgd = legend('Baseline', 'Approach 1', 'Approach 2');
    lgd.FontSize = 12;
    
    figure
    plot([0.5;4.5], db(1:2,2), 'LineWidth',3);
    ylim([0,1]);
    hold on
    bar(alpha_vals, [d1(:,2),d2(:,2)]);
    xlabel('\alpha', 'FontSize', 20);
    title(strcat('Recall (k=',num2str(k),')'), 'FontSize', 16);
    lgd = legend('Baseline', 'Approach 1', 'Approach 2');
    lgd.FontSize = 12;
    
    figure
    plot([0.5;4.5], db(1:2,3), 'LineWidth',3);
    ylim([0,1]);
    hold on
    bar(alpha_vals, [d1(:,3),d2(:,3)]);
    xlabel('\alpha', 'FontSize', 20);
    title(strcat('F-score (k=',num2str(k),')'), 'FontSize', 16);
    lgd = legend('Baseline', 'Approach 1', 'Approach 2');
    lgd.FontSize = 12;
    
end

