"""
rightsizing_score — 클라우드 컴퓨트 자원 적정성 점수 산출 (standalone, 의존성 없음).

파이프라인:  MetricSource → clean → extract_features → Scorer → InstanceScore
점수 출력은 그대로 LLM 추천 엔진의 입력 컨텍스트가 된다.
"""
from .config import ScoringConfig
from .datasource import AssetComputeMetricSource, InMemorySource, MetricSource
from .models import (
    Action,
    CleanedMetrics,
    CleaningReport,
    DailyShape,
    FeatureVector,
    InstanceScore,
    MetricSample,
    RawMetrics,
    ResourceFeatures,
    TimeWindow,
)
from .pipeline import PipelineTrace, RightsizingPipeline
from .scoring import RuleScorer, Scorer
from .shape import analyze_shape
from .explain import render_trace, to_llm_context

__all__ = [
    "ScoringConfig",
    "MetricSource", "InMemorySource", "AssetComputeMetricSource",
    "RawMetrics", "MetricSample", "TimeWindow",
    "CleanedMetrics", "CleaningReport",
    "FeatureVector", "ResourceFeatures", "DailyShape",
    "InstanceScore", "Action",
    "Scorer", "RuleScorer",
    "RightsizingPipeline", "PipelineTrace",
    "analyze_shape", "render_trace", "to_llm_context",
]
