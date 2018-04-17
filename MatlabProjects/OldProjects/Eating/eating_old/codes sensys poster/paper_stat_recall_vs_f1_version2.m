x = 0.9:0.01:0.98
y = [0.783, 0.783, 0.782, 0.796, 0.792, 0.804, 0.806, 0.794, 0.77]
figure
plot(x,y, 'LineWidth', 3);
hold on
grid on
scatter(x,y, 'LineWidth', 3);
%h_legend=legend('F1-Score','Y','Z');
%set(h_legend,'FontSize',20);

a = get(gca,'XTickLabel');
set(gca,'XTickLabel',a,'fontsize',16)

xlabel('Lower bound on recall at segmentation', 'FontSize', 32);
ylabel('Overall F1-Score', 'FontSize', 32);


